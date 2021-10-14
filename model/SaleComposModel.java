package ru.kazachkov.florist.model;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableDouble;
import android.databinding.ObservableInt;
import android.support.v4.util.Pair;
import android.view.View;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;
import ru.kazachkov.florist.R;
import ru.kazachkov.florist.api.model.Category;
import ru.kazachkov.florist.api.model.GoodDT;
import ru.kazachkov.florist.api.model.GoodSMDiscDT;
import ru.kazachkov.florist.api.model.Order2Seller;
import ru.kazachkov.florist.api.model.OrderDetailed;
import ru.kazachkov.florist.api.model.OrderGlance;
import ru.kazachkov.florist.api.model.Photo;
import ru.kazachkov.florist.api.model.request.AddUpdateOrderPhotoAuth;
import ru.kazachkov.florist.app.AppPreferences;
import ru.kazachkov.florist.composition.CompositionContract;
import ru.kazachkov.florist.composition.InventoryCountDialog;
import ru.kazachkov.florist.data.DataController;
import ru.kazachkov.florist.interfaces.BindDetailed;
import ru.kazachkov.florist.logic.Error;
import ru.kazachkov.florist.sales.EFastAction;
import ru.kazachkov.florist.tools.SaleMode;
import ru.kazachkov.florist.tools.Utils;
import ru.kazachkov.florist.viewmodel.impl.ComponentVMImpl;
import ru.kazachkov.florist.viewmodel.impl.GoodsVMImpl;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

public class SaleComposModel implements ComponentVMImpl.Callback, CompositionContract.Presenter {
    private static final String TAG = SaleComposModel.class.getCanonicalName();


    private final SaleMode saleMode;
    private final CompositionContract.View view;
    private final AppPreferences appPreferences;
    private final DataController dataController;

    @Setter
    private double discount;

    @Setter
    private EFastAction orderAction = EFastAction.NONE;

    private final CompositeSubscription subscriptions = new CompositeSubscription();
    private Subscription filterGoodsModelSubscription = Subscriptions.empty();
    private double price;

    public SaleComposModel(CompositionContract.View view,
                           AppPreferences appPreferences,
                           DataController dataController,
                           SaleMode saleMode,
                           EFastAction orderAction) {
        this.dataController = dataController;
        this.saleMode = saleMode;
        this.view = view;
        this.appPreferences = appPreferences;
        this.orderAction = orderAction;
        view.showHomeTitle(saleMode.getName());
    }

    @Getter
    private ObservableArrayList<ComponentVMImpl> componentVMs = new ObservableArrayList<>();

    private static final String NOT_SELECTED_GROUP = "not_selected";

    @Getter
    private ObservableArrayList<Category> groups;

    @Getter
    private BigDecimal basePrice = BigDecimal.ZERO;

    @Getter
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Getter
    private ObservableBoolean fabShowed = new ObservableBoolean();
    @Getter
    private ObservableBoolean pagerShowed = new ObservableBoolean();

    private String searchQuery = "";

    @Getter
    private List<GoodsModel> goodsModels = new ArrayList<>();

    private ComponentVMImpl deleteCandidate;

    private String barcode;
    private Category currentGroup;
    private OrderGlance orderGlance;
    @Getter
    private ObservableInt countItems = new ObservableInt();
    @Getter
    private boolean savedOrder = false;
    @Getter
    private BindDetailed orderDetailed;
    @Getter
    private boolean movePurchase;
    private BehaviorSubject<Object> updateGoodsListSubject = BehaviorSubject.create(new Object());


    public final ObservableBoolean isSumDiffers = new ObservableBoolean(false);
    public final ObservableDouble oldSum = new ObservableDouble(0);



    private Observable<List<GoodsModel>> filterGoodsModels(List<GoodsModel> goodsModels,
                                                           final String searchQuery,
                                                           final String salePointId,
                                                           final Category filterCategory,
                                                           SaleMode saleMode) {
        String[] arr = searchQuery.split(" ");
        List<String> searchQueryWords = Arrays.asList(arr);

        return Observable.from(goodsModels)
                .filter(goodsModel -> goodsModel.getItemPrices().getStorage1CId().equals(salePointId))
                .filter(goodsModel1 -> searchFunc(searchQueryWords).call(goodsModel1))
                .filter(goodsModel -> {
                    if (filterCategory == null) return true;
                    List<Category> categories = goodsModel.getCategories();
                    if (categories == null || categories.size() == 0) return false;
                    for (Category category : categories)
                        if (filterCategory.getCategory1CId().equals(category.getCategory1CId()))
                            return true;
                    return false;
                })
                .map(goodsModel -> {
                    goodsModel.setSaleMode(saleMode);
                    return goodsModel;
                })
                .toSortedList((goodsVM, goodsVM2) -> {
                    if (goodsVM.getInStock() ==  goodsVM2.getInStock()) {
                        return goodsVM.getItem().getFlowerName().compareTo(goodsVM2.getItem().getFlowerName());
                    } else {
                        return Integer.valueOf(goodsVM2.getInStock()).compareTo(goodsVM.getInStock());
                    }
                });

    }

    private <T extends GoodsModel> Func1<T, Boolean> searchFunc(List<String> query) {
        return (t) -> {
            for (String word : query) {
                if (!t.getName().toLowerCase().contains(word.toLowerCase())) {
                    return false;
                }
            }
            return true;
        };
    }


    public void onNextSearchQuery(String text) {
        searchQuery = text;
        onFilter();
    }

    private void onFilter() {
        filterGoodsModels();
    }

    public void add(String flowerId, int count) {
        Observable.from(goodsModels)
                .filter(goodsModel -> goodsModel.getItem().getFlower1CId().equals(flowerId))
                .first()
                .repeat(count)
                .subscribe(this::add);
    }

    @Override
    public void updateSum() {
        if (saleMode == SaleMode.INVENTORY) {

            Observable<BigDecimal> sum = Observable.from(componentVMs)
                    .map(componentVM -> componentVM.getModel().getSum(saleMode));

            Observable<BigDecimal> positive = sum.filter(bigDecimal -> bigDecimal.compareTo(BigDecimal.ZERO) > 0).defaultIfEmpty(BigDecimal.ZERO);

            Observable<BigDecimal> negative = sum.filter(bigDecimal -> bigDecimal.compareTo(BigDecimal.ZERO) < 0).defaultIfEmpty(BigDecimal.ZERO);

            subscriptions.add(positive.reduce(BigDecimal::add)
                    .zipWith(negative.reduce(BigDecimal::add), Pair::create)
                    .subscribe(pair -> {
                        view.showDifferenceOf(pair.first.abs(), pair.second.abs());
                        basePrice = pair.first;
                        totalPrice = pair.first;
                    }, throwable -> {
                    }));
        } else {
            subscriptions.add(Observable.from(componentVMs)
                    .map(componentVM -> componentVM.getModel().getSum(saleMode))
                    .reduce(BigDecimal::add)
                    .subscribe(value -> {
                        view.showAmountOf(value);
                        //basePrice = value;
                        totalPrice = value;

                        isSumDiffers.set(oldSum.get() != value.doubleValue());

                    }, throwable -> {
                    }));
        }

        view.showOldSum(new BigDecimal(oldSum.get()));
    }



    @Override
    public int getPosition(ComponentVMImpl componentVM) {
        if (componentVMs.contains(componentVM)) {
            return componentVMs.indexOf(componentVM);
        }
        return -1;
    }

    @Override
    public void onShowPager(ComponentVMImpl componentVM) {
        try {
            List<Photo> photo = componentVM.getModel().getGoodsModel().getPhoto();
            List<String> first = Observable.from(photo).map(Photo::getPath).toList().toBlocking().first();
            view.showPhotosScreen(first, 0);
        } catch (NullPointerException e) {
            view.showError(new NullPointerException("Не удалось найти фото"));
        }

    }

    @Override
    public void showInventoryDialog(InventoryCountDialog.OnResultListener onResultListener, String name, BigDecimal price, List<BigDecimal> terms) {
        view.showInventoryDialog(onResultListener, name, price, terms);
    }

    @Override
    public void showCountDialog(ComponentVMImpl componentVM, BigDecimal count, BigDecimal price) {
        view.showCountDialog(this, componentVM.getModel().getGoodsModel(), componentVM, count, price, false);
    }

    //TODO remove showMessage flag
    public boolean add(ComponentVMImpl componentVM, boolean showMessage) {
        deleteOld();
        boolean added = false;
        if (showMessage) {
            view.clearFocusSearchView();
        }
        if (!movePurchase) {
            if (!contains(componentVMs, componentVM)) {
                componentVMs.add(componentVM);
                fabShowed.set(componentVMs.size() > 0);
                countItems.set(componentVMs.size());
                added = true;
                basePrice = basePrice.add(new BigDecimal(componentVM.getModel().getBaseSum()));
                //oldSum.set(oldSum.get() + componentVM.getModel().getSum());
            } else {
                if (showMessage) {
                    view.showToast(R.string.alredy_added);
                }
            }
            updateSum();
        }
        return added;
    }

    private boolean contains(List<ComponentVMImpl> componentVMs, ComponentVMImpl componentVM) {
        for (ComponentVMImpl item : componentVMs) {
            if (item.getFlowerId().equals(componentVM.getFlowerId())) {
                return true;
            }
        }
        return false;
    }

    public void add(GoodsVMImpl goodsVM) {
        ComponentVMImpl componentVM = ComponentVMImpl.create(goodsVM.getGoodsModel(), this);
        add(componentVM, true);
    }


    @Override
    public void addGoodsToComposition(GoodsVMImpl goodsVM) {
        subscriptions.add(Observable.from(componentVMs)
                .map(ComponentVMImpl::getFlowerId)
                .contains(goodsVM.getGoodsModel().getItem().getFlower1CId())
                .subscribe(aBoolean -> {
                    if (!aBoolean) {
                        //addGoodToList(goodsVM);
                        showOnAddGoodDialog(goodsVM);
                    }
                }));
    }

    private void showOnAddGoodDialog(GoodsVMImpl goodsVM){
        ComponentVMImpl componentVM = ComponentVMImpl.create(goodsVM.getGoodsModel(), this);

        if (saleMode == SaleMode.INVENTORY) {
            view.showInventoryDialog(componentVM,
                    goodsVM.title(),
                    new BigDecimal(componentVM.getPrice()),
                    componentVM.getModel().getTerms());
        } else {
            view.showCountDialog(this, goodsVM.getGoodsModel(), componentVM, null, null, true);
        }
    }



    @Override
    public boolean onEditClick(View view, GoodsVMImpl goodsVM) {
        this.view.showEditGoodsScreen(goodsVM.getGoodsModel().getItem().getFlower1CId());
        return false;
    }

    public void add(GoodsModel goodsModel) {
        add(ComponentVMImpl.create(goodsModel, this), true);
    }


    @Override
    public boolean isPurchase() {
        return saleMode == SaleMode.PURCHASE;
    }

    @Override
    public SaleMode getSaleMode() {
        return saleMode;
    }

    @Override
    public void delete(ComponentVMImpl componentVM) {
        deleteOld();
        Observable.just(componentVM)
                .delay(3, TimeUnit.SECONDS, Schedulers.io())
                .filter(componentVM1 -> componentVMs.contains(componentVM1))
                .filter(componentVM1 -> deleteCandidate != null)
                .filter(componentVM1 -> deleteCandidate.equals(componentVM1))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(componentVM1 -> {
                    componentVMs.remove(componentVM);
                    countItems.set(componentVMs.size());
                });
        deleteCandidate = componentVM;
    }

    @Override
    public void undoDelete(ComponentVMImpl componentVM) {
        deleteCandidate = null;
    }

    private void deleteOld() {
        if (deleteCandidate != null) {
            Subscription subscription = Observable.just(deleteCandidate)
                    .filter(componentVM1 -> componentVMs.contains(componentVM1))
                    .subscribe(componentVM1 -> {
                        componentVMs.remove(componentVM1);
                        countItems.set(componentVMs.size());
                    }, throwable -> {
                    });

            subscriptions.add(subscription);

        }
    }

    public void hasError(Action1<Integer> callback) {
        Observable.from(componentVMs)
                .map(componentVM -> componentVM.hasError() ? 1 : 0)
                .reduce((integer, integer2) -> integer + integer2)
                .subscribe(callback, throwable -> {
                });
    }

    public boolean hasError() {
        boolean hasError = false;
        for (ComponentVMImpl component : componentVMs) {
            if (component.hasError()) {
                hasError = true;
            }
        }
        return hasError;
    }

    public void setBarcode(String barcode) {
        Observable<GoodsModel> barCodeObs = Observable.from(goodsModels)
                .filter(goodsModel -> goodsModel.getItem().getFlowerArticle().contains(barcode));

        barCodeObs.subscribe(this::add, throwable -> {
        });

        barCodeObs.isEmpty().subscribe(aBoolean -> {
            view.showToast(aBoolean ? R.string.goods_added : R.string.goods_not_found);
        });
    }

    private List<GoodDT> getGoodsDT() {
        List<GoodDT> goodDTList = new ArrayList<>();
        for (ComponentVMImpl component : componentVMs) {
            goodDTList.add(component.getModel().getGoodDT());
        }
        return goodDTList;
    }


    public void onPagerHide() {
        pagerShowed.set(false);
    }

    public void setCurrentGroup(int position) {
        if (position == 0) {
            if (currentGroup != null) {
                currentGroup = null;
                onFilter();
            }
        } else if (groups != null && groups.size() > position) {
            if (this.currentGroup == null || !this.currentGroup.equals(groups.get(position))) {
                this.currentGroup = groups.get(position);
                onFilter();
            }
        }
    }




    public void bind(BindDetailed orderDetailed) {
        this.orderDetailed = orderDetailed;
        this.savedOrder = true;
        this.movePurchase = orderDetailed.isPurchase() && (orderDetailed.getDocType() == 201);
    }

    public boolean empty() {
        return componentVMs.size() == 0;
    }


    public void setRegularPrices() {
        Observable.from(componentVMs).forEach(componentVM -> {
            componentVM.setRegularPrice();
            componentVMs.set(getPosition(componentVM), componentVM);
        });
    }


    @Override
    public void subscribe() {

        subscriptions.clear();
        this.goodsModels.clear();

        Subscription subscription = dataController.getGroupsObs()
                .subscribe(categories -> {
                    this.groups = new ObservableArrayList<>();
                    this.groups.add(new Category(NOT_SELECTED_GROUP, "Категория"));
                    this.groups.addAll(categories);
                }, Error.onError());
        subscriptions.add(subscription);


        Subscription allGoodsSubscription = getGoodsModelsList()
                .debounce(2, TimeUnit.SECONDS)
                .subscribe(goodsModel -> {
                    this.goodsModels.clear();
                    goodsModels.addAll(goodsModel);
                    filterGoodsModels();
                }, view::showError);

        subscriptions.add(allGoodsSubscription);


        if (orderDetailed != null && orderDetailed.getGoods() != null
                && orderDetailed.getGoods().size() != 0) {

            Subscription componentsSubscription = getGoodsModelsList().flatMap(goodsModels1 -> Observable.from(goodsModels1)
                    .toMap(goodsModel -> goodsModel.getItem().getFlower1CId()))
                    .switchMap(stringGoodsModelMap -> Observable.from(orderDetailed.getGoods())
                            .map(goodDT -> ComponentVMImpl.create(stringGoodsModelMap.get(goodDT.getFlower1CId()), goodDT, this)))
                    .subscribe(componentVMs1 -> {
                        add(componentVMs1, false);
                        //oldSum.set(oldSum.get() + componentVMs1.getModel().getSum());
                        oldSum.set(orderDetailed.getSumFinal());
                        view.showOldSum(BigDecimal.valueOf(oldSum.get()));
                    }, view::showError);

            subscriptions.add(componentsSubscription);
        }
    }

    private void filterGoodsModels() {
        filterGoodsModelSubscription.unsubscribe();

        Observable<List<GoodsModel>> goodsModelObs = filterGoodsModels(goodsModels,
                searchQuery,
                appPreferences.getSalePntId(),
                currentGroup,
                saleMode);

        filterGoodsModelSubscription = goodsModelObs
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(goods -> {
                    view.showGoods(goods);
                    updateSum();
                }, view::showError);

        subscriptions.add(filterGoodsModelSubscription);
    }

    private Observable<List<GoodsModel>> getGoodsModelsList() {
        return updateGoodsListSubject
                .switchMap(aVoid -> dataController.getGoodsModelsList(appPreferences.getSalePntId()));
    }


    @Override
    public void showImage(GoodsVMImpl goodsVM) {
        try {
            List<Photo> photo = goodsVM.getGoodsModel().getPhoto();
            List<String> first = Observable.from(photo).map(Photo::getPath).toList().toBlocking().first();
            view.showPhotosScreen(first, 0);
        } catch (NullPointerException e) {
            view.showError(new NullPointerException("Не удалось найти фото"));
        }
    }

    @Override
    public void unsubscribe() {
        subscriptions.clear();
    }

    @Override
    public void startCashOrder() {
        view.hideFabsGroup();
        view.showScreenOrder(getGoodsDT(), basePrice.toString(), totalPrice.toString(), saleMode, orderDetailed, 1, discount);
    }

    @Override
    public void startCashlessOrder() {
        view.hideFabsGroup();
        view.showScreenOrder(getGoodsDT(), basePrice.toString(), totalPrice.toString(), saleMode, orderDetailed, 2, discount);
    }

    @Override
    public void startCommonOrder() {
        view.hideFabsGroup();
        view.showScreenOrder(getGoodsDT(), basePrice.toString(), totalPrice.toString(), saleMode, orderDetailed, 0, discount);
    }

    @Override
    public void startSelectOrderVariant() {
        view.showFabGroup(orderAction);
    }

    @Override
    public void updateGoodsModel(String flowerId) {
        updateGoodsListSubject.onNext(new Object());
    }

    @Override
    public void saveOrder() {
        if (orderDetailed instanceof OrderDetailed) {
            view.showProgress();
            ((OrderDetailed) orderDetailed).setGoods(getGoodsDT());
            ((OrderDetailed) orderDetailed).setOrderDate(Utils.currentDateUTC());
            AddUpdateOrderPhotoAuth addUpdateOrderPhotoAuth = AddUpdateOrderPhotoAuth.create(appPreferences.getSessionId(), appPreferences.getSalePntId(), (OrderDetailed) orderDetailed);
            Subscription subscription = dataController.addUpdateOrderPhotoAuth(addUpdateOrderPhotoAuth)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnEach(addUpdateOrderResponse -> view.hideProgress())
                    .subscribe(addUpdateOrderResponse -> {
                        view.backToMainScreen();
                    }, view::showError);
            this.subscriptions.add(subscription);
        } else if (orderDetailed instanceof Order2Seller) {
            throw new NullPointerException("no implemented method save order for Order2Seller");

        }
    }

    public void reverseOldAndTotalSum(){
        setNewTotalPrice(oldSum.get());
    }

    public void setNewTotalPrice(double newPrice) {
        oldSum.set(totalPrice.doubleValue());
        double multiplier = newPrice / totalPrice.doubleValue();

        for (ComponentVMImpl componentVM : componentVMs) {
            componentVM.recountValuesAccordingTo(multiplier);
        }

        updateSum();
    }



    public double getPrice() {
        return price;
    }
}
