package ru.kazachkov.florist.model.impl;

import android.app.ProgressDialog;
import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import ru.kazachkov.florist.R;
import ru.kazachkov.florist.SaleCompositionActivity;
import ru.kazachkov.florist.api.model.CompanyAtGlanceDT;
import ru.kazachkov.florist.api.model.FastUpdate;
import ru.kazachkov.florist.api.model.GoodDT;
import ru.kazachkov.florist.api.model.OrderDetailed;
import ru.kazachkov.florist.api.model.OrderDetailedData4;
import ru.kazachkov.florist.api.model.UserAuthRole;
import ru.kazachkov.florist.api.model.request.AddUpdateOrderPhotoAuth;
import ru.kazachkov.florist.api.model.request.EffectFastActionActivity;
import ru.kazachkov.florist.api.model.request.EffectFastActionActivity4;
import ru.kazachkov.florist.api.model.request.GetOrder2SellerDetailedAuth;
import ru.kazachkov.florist.api.model.request.GetOrderListAuth;
import ru.kazachkov.florist.api.model.response.OrdersListAction;
import ru.kazachkov.florist.app.AppPreferences;
import ru.kazachkov.florist.data.DataController;
import ru.kazachkov.florist.interfaces.BindDetailed;
import ru.kazachkov.florist.interfaces.IOrderGlance;
import ru.kazachkov.florist.interfaces.OrderInfo;
import ru.kazachkov.florist.logic.Error;
import ru.kazachkov.florist.model.md.SaleModel;
import ru.kazachkov.florist.order.OrderActivity;
import ru.kazachkov.florist.order.paymentdata.PaymentDataPresenter;
import ru.kazachkov.florist.sales.EFastAction;
import ru.kazachkov.florist.sales.OrderDataDialogInfo;
import ru.kazachkov.florist.sales.SalesContract;
import ru.kazachkov.florist.tools.SaleMode;
import ru.kazachkov.florist.tools.SearchCategory;
import ru.kazachkov.florist.tools.Utils;
import ru.kazachkov.florist.viewmodel.impl.SaleVMImpl;
import ru.kazachkov.florist.viewmodel.vm.SaleVM;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;

public class MainModelImpl implements SalesContract.Model {
    @Getter
    final SalesContract.View view;
    private final DataController dataController;
    private final String sessionId;
    private final AppPreferences prefs;

    @Getter
    private List<OrdersListAction> operations = new ArrayList<>();
    @Getter
    private List<UserAuthRole.SalePnt> salePnts = new ArrayList<>();

    @Getter
    private ObservableField<String> sum = new ObservableField<>();
    @Getter
    private ObservableBoolean showProblem = new ObservableBoolean();

    @Getter
    private UserAuthRole.SalePnt currentSalePnt;

    @Getter
    private ObservableBoolean loading = new ObservableBoolean(false);

    @Getter
    private ObservableField<String> startDate = new ObservableField<>();
    @Getter
    private ObservableField<String> endDate = new ObservableField<>();

    private SearchCategory searchCategory;
    private DateTime dateTime;
    private DateTime dateEndTime;

    private BehaviorSubject<DateTime> dateTimeSubject = BehaviorSubject.create();
    private BehaviorSubject<DateTime> dateEndTimeSubject = BehaviorSubject.create();
    private BehaviorSubject<Boolean> purchaseModeSubject = BehaviorSubject.create();
    private BehaviorSubject<UserAuthRole.SalePnt> salePntSubject = BehaviorSubject.create();
    private BehaviorSubject<Integer> updateSubject = BehaviorSubject.create();

    private CompositeSubscription subscription = new CompositeSubscription();

    public MainModelImpl(SalesContract.View view, DataController dataController, AppPreferences appPreferences, boolean modePurchase) {
        this.view = view;
        this.dataController = dataController;
        this.sessionId = appPreferences.getSessionId();
        this.prefs = appPreferences;
        purchaseModeSubject.onNext(modePurchase);
    }

    @Override
    public void start() {
        this.subscription.clear();
        if (purchaseModeSubject.getValue()) {
            setBeginDateTime(new DateTime().minusDays(6));
            setEndDateTime(new DateTime());
        } else {
            DateTime dt = new DateTime();
            setBeginDateTime(dt);
            setEndDateTime(dt);
        }


        updateSubject.onNext(1);

        Subscription salePointsSubs = dataController.getSalePt()
                .flatMap(salePnts1 -> Observable.from(salePnts1)
                        .distinct(UserAuthRole.SalePnt::getStorageName)
                        .toList())
                .subscribe(salePnts -> {
                    this.salePnts.clear();
                    this.salePnts.addAll(salePnts);
                    view.showSalePnts(salePnts);
                }, throwable -> {
                });


        Subscription actions = dataController.getOrderListActions()
                .subscribe(ordersListActions -> {
                    operations.clear();
                    operations.addAll(ordersListActions);
                    view.showOperations(ordersListActions);
                }, Error.onError());

        Subscription orderListSubs = getOrderInfoObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(ordersReportData -> {
                    view.clearItems();
                    sum.set(Utils.htmlTotalSum(
                            ordersReportData.getTotalOrderSum(),
                            ordersReportData.getTotalCashPaid(),
                            ordersReportData.getTotalBankPaid()));
                })
                .observeOn(Schedulers.newThread())
                .map(OrderInfo::getOrderList)
                .flatMap(orderGlances -> Observable.from(orderGlances)
                        .map((Func1<IOrderGlance, SaleModel>) SaleModelImpl::new)
                        .map(SaleVMImpl::new)
                        .map(saleVM -> (SaleVM) saleVM)
                        //.toMultimap(SaleVM::dateTime)
                        .toList()
                        )
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(notification -> loading.set(false))
                .doOnEach(notification -> {
                    view.showUpdateIcon(notification.hasThrowable());
                })
                .subscribe(newSaleVM -> {
                    subscribeOnUpdateTime();
                    view.showItems(newSaleVM);
                }, Error.onError(view.getContext(), throwable -> loading.set(false)));

        this.subscription.add(salePointsSubs);
        this.subscription.add(orderListSubs);
        this.subscription.add(actions);
    }

    @NonNull
    private Observable<OrderInfo> getOrderInfoObservable() {
        return Observable.combineLatest(updateSubject,
                salePntSubject,
                purchaseModeSubject,
                dateTimeSubject,
                dateEndTimeSubject,
                view.getSelectedOrderListActionObservable(),
                (update, pnt, purch, start, end, listAction) -> {
                    view.setCurrentListType(listAction.toString(), listAction, purch);
                    /*if (purch) {
                        return GetOrderSellerListAuth.create(sessionId, start.toString(), end.toString(), pnt.getStorage1CId());
                    }*/

                    return GetOrderListAuth.create(sessionId, dateTime.toString(), dateEndTime.toString(), pnt.getStorage1CId(), listAction.getOrderListSequence());
                })
                .doOnNext(ordersRequestData -> loading.set(true))
                .flatMap(orderRequestData ->
                        //dataController.getOrders(orderRequestData).subscribeOn(Schedulers.io()).retry(5)
                        dataController.getOrders(orderRequestData).subscribeOn(Schedulers.io()).retry(5)
                );
    }


    private void subscribeOnUpdateTime() {
        this.subscription.add(Utils.updatedAgoText(0)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::showUpdateTime));
    }


    @Override
    public void stop() {
        subscription.clear();
    }

    @Override
    public ObservableBoolean isProgress() {
        return loading;
    }


    @Override
    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
        startDate.set(Utils.prepareDate(dateTime.toString(), "dd MMMM YYYY"));
        this.dateEndTime = dateTime;
        endDate.set(Utils.prepareDate(dateTime.toString(), "dd MMMM YYYY"));
        view.showCurrentDateButtonInHeader(Utils.prepareDate(dateTime.toString(), "dd MMMM YYYY"));
        dateTimeSubject.onNext(dateTime);
    }

    @Override
    public void setBeginDateTime(DateTime dateTime) {
        dateTimeSubject.onNext(dateTime);
        this.dateTime = dateTime;
        startDate.set(Utils.prepareDate(dateTime.toString(), "dd MMMM YYYY"));
    }

    @Override
    public void setEndDateTime(DateTime dateTime) {
        dateEndTimeSubject.onNext(dateTime);
        this.dateEndTime = dateTime;
        endDate.set(Utils.prepareDate(dateTime.toString(), "dd MMMM YYYY"));
    }

    @Override
    public Date getDateTime() {
        return dateTime.toDate();
    }


    @Override
    public Date getEndDateTime() {
        return dateEndTime.toDate();
    }



    @Override
    public void setIsPurchase(boolean purchase) {
        /*if (!purchase) {
            dateEndTimeSubject.onNext(new DateTime());
        } else {
            dateEndTimeSubject.onNext(dateEndTime);
        }*/
        //purchaseModeSubject.onNext(purchase);
    }


    @Override
    public void selectSearch() {
        view.showSearchCategoryWindow();
        if (purchaseModeSubject.getValue()) {
            view.showSelectDateGroup();
        }
    }

    @Override
    public void closeSearch() {
        searchCategory = null;
       // if (purchaseModeSubject.getValue()) {
            view.hideSelectDateGroup();
       // }
    }

    @Override
    public void open(SaleModel saleModel, SaleMode saleMode) {
        Context context = view.getContext();
        ProgressDialog progressDialog = ProgressDialog.show(context,
                context.getResources().getString(R.string.handle_requst),
                context.getResources().getString(R.string.receiving_goods));
        progressDialog.show();

        Subscription subscription = getLoadDetailedOrderObserbable(saleModel)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnEach(notification -> {
                    progressDialog.hide();
                })
                .subscribeOn(Schedulers.io())
                .subscribe(order -> {
                    SaleCompositionActivity.start(context, order, saleMode);
                }, e -> {
                    e.printStackTrace();
                });

        this.subscription.add(subscription);
    }

    @NonNull
    private Observable<BindDetailed> getLoadDetailedOrderObserbable(SaleModel saleModel) {
        PaymentDataPresenter.currentOrderDate = saleModel.dateTime().toString();
        return Observable.just(saleModel.getOrderGlance().isPurchase())
                .switchMap((Boolean aBoolean) -> {
                    if (aBoolean) {
                        return dataController.getFloristApi().getOrder2SellerDetailedAuth(
                                GetOrder2SellerDetailedAuth.create(sessionId,
                                        currentSalePnt.getStorage1CId(),
                                        saleModel.getOrderGlance().getOrder1CId(),
                                        saleModel.getOrderGlance().getOrder1CDate()
                                )).map(orderSellerDetaildAuth -> orderSellerDetaildAuth.getBody().getResponse().getGetOrderListAuthResult().getOrder2Seller())
                                .subscribeOn(Schedulers.io());
                    } else return
                            dataController.getFloristApi().effectFastActionActivity4(
                                    EffectFastActionActivity4.create(
                                            new EffectFastActionActivity4.Data(
                                                    sessionId, currentSalePnt.getStorage1CId(),
                                                    saleModel.getDocType(), 15,
                                                    convertDateFromOld(saleModel.getOrderGlance().getOrder1CDate()),
                                                    saleModel.getOrderGlance().getOrder1CId()
                                            )
                                    )
                            )
                                    .map(effectFastActionActivity4Response -> {
                                        return effectFastActionActivity4Response.getOrderDetailedData4();
                                    })
                                    .map(OrderDetailedData4::convertNewOrderToOld)
                                    .subscribeOn(Schedulers.io());
                });
    }



    private String convertDateFromOld(String old) {
        String sub = old.split("T")[0];
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        String dateStr = null;
        try {
            Date date = df.parse(sub);
            DateFormat newDf = new SimpleDateFormat("dd.mm.yyyy");
            dateStr = newDf.format(date);
        } catch (Exception e) {
            //
        }
        return dateStr;
    }

    @Override
    public void openActionMenuFor(View anchorView, SaleModel saleModel) {
        subscription.add(dataController.getOrderFastActions(saleModel.getDocType())
                .subscribe(orderFastActions -> {
                    view.showPopupMenu(anchorView, saleModel, orderFastActions);
                }, Error.onError(view.getContext())));
    }

    @Override
    public void selectSearchCategory(SearchCategory searchCategory) {
        this.searchCategory = searchCategory;
    }

    @Override
    public void searchCategoryWidowDismiss() {
        if (searchCategory == null) {
            closeSearch();
            view.closeSearch();
        }
    }

    @Override
    public void startCompositionScreen(SaleMode saleMode) {
        switch (saleMode) {
            case INVENTORY:
                view.showUpdateDialog();
                break;
            case PREPAY:
                ArrayList<GoodDT> listGoods = new ArrayList<>();

                String flowerFakeId = dataController.getRealmFakeItem();

                listGoods.add(new GoodDT(flowerFakeId, 1, 1, 2));

                OrderActivity.start(view.getContext(), SaleMode.PREPAY, "1000", "1000", listGoods, null, 0, 0);

                break;
            default:
                SaleCompositionActivity.start(view.getContext(), saleMode);
                break;
        }
    }

    @Override
    public void updateGoodsAndOpenInventory() {
        view.showProgressFastUpdate();
        subscription.add(fastUpdateObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(fastUpdate -> {
                    view.stopProgressFastUpdate();
                    SaleCompositionActivity.start(view.getContext(), SaleMode.INVENTORY);
                }, throwable -> {
                    view.stopProgressFastUpdate();
                    view.showError(throwable);
                }));
    }

    @Override
    public void showOrderDataDialog(SaleModel saleModel) {
        IOrderGlance orderGlance = saleModel.getOrderGlance();
        BehaviorSubject<String> initClientSubject = BehaviorSubject.create();

        initClientSubject.onNext("");

        //Пока берем телефон из Realm кеша
        Observable<List<CompanyAtGlanceDT>> companiesList = dataController.getCompaniesList(null, false);


        companiesList
                .first(companyAtGlanceDTs1 ->
                        companyAtGlanceDTs1.size() != 0)
                .flatMap(companyAtGlanceDTs12 ->
                        Observable.just(companyAtGlanceDTs12).zipWith(initClientSubject, Pair::create)
                        .flatMap(listStringPair -> {
                            if (TextUtils.isEmpty(listStringPair.second))
                                return Observable.from(listStringPair.first).filter(companyAtGlanceDT ->
                                        companyAtGlanceDT.getCompany1CId().equals(saleModel.getOrderGlance().getClientId())).first();
                            return Observable.from(listStringPair.first)
                                    .firstOrDefault(new CompanyAtGlanceDT(listStringPair.second, "ОТСУТВУЕТ В СПИСКЕ", 1, 1,"","",0.0,0.0,"", ""), companyAtGlanceDT -> companyAtGlanceDT.getCompany1CId().equals(listStringPair.second));

                        })).first().subscribe( oneCompany -> {

                        OrderDataDialogInfo orderDataDialogInfo = OrderDataDialogInfo.builder()
                                .compositionInfo(orderGlance.getItemString())
                                .orderDate(Utils.dateTimeOf(orderGlance.getOrder1CDate()))
                                .orderId(orderGlance.getOrder1CId())
                                .clientName(saleModel.getOrderGlance().getClientName())
                                .clientNumber(oneCompany.getTelNumber())
                                .comment(orderGlance.getCommentString())
                                .discountValue(5)
                                .floristName(saleModel.getOrderGlance().getResponciblePersonName())
                                .paidSum(orderGlance.getPaidDataHint().getPaidSum())
                                .totalSum(orderGlance.getPaidDataHint().getTotalSum())
                                .build();
                        view.showOrderDataDialog(saleModel, orderDataDialogInfo);

                });


    }

    @Override
    public void operationSelected(OrdersListAction item) {

    }

    @Override
    public void salePointSelected(UserAuthRole.SalePnt item) {
        if (this.currentSalePnt == null || !this.currentSalePnt.equals(item)) {
            this.currentSalePnt = item;
            salePntSubject.onNext(currentSalePnt);
            prefs.setSalePnt(currentSalePnt);
        }
    }

    @Override
    public void selectedActionForSaleModel(EFastAction orderAction, SaleModel saleModel) {
        Context context = view.getContext();
        ProgressDialog progressDialog = ProgressDialog.show(context, "Секундочку", "Обработка запроса");
        progressDialog.show();

        switch (orderAction) {
            case EDIT_COMPOSITION:
                /**На клиенте открываем окно с составом заказа.
                 * Потом переходим к Итоговому, где пользователь
                 * сохраняет изменения, а также может отредактировать
                 * дополнительную информацию.  Возможно, следует на экране
                 * с составом заказа для плавающей кнопки в этом случае добавить
                 * действия «Сохранить» (завершить работу с заказом) без перехода к Итоговому.
                 */


                Subscription subscription = getLoadDetailedOrderObserbable(saleModel)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnEach(notification -> {
                            progressDialog.dismiss();
                        })
                        .subscribeOn(Schedulers.io())
                        .subscribe(order -> {
                            SaleCompositionActivity.start(context, order, SaleMode.SALE, orderAction);
                        });
                this.subscription.add(subscription);
                break;
            case CHANGE_PROVIDER:
                /**
                 * В этом случае сразу идем к Итоговой странице и устанавливаем фокус на области с поставщиком.
                 * Видимо, с сервера придет только итоговая информация. Это ускорит работу.
                 * Если пользователь захочет перейти к составу заказа, то вернем полную информацию или только о составе заказа.
                 */
                progressDialog.dismiss();
                OrderActivity.start(view.getContext());
                break;
//            case EXTRA_INFORMATION:
//                progressDialog.dismiss();
//                OrderActivity.start(view.getContext());
//                break;
            case MOVE:
                /**
                 * Закупленный товар можно полностью или частично переместить по другим торговым точкам.
                 * Закупка фиксирует расход денег и поступление товара, а перемещение только изменяет остатки.
                 * При выборе этого пункта переходим к созданию нового документа перемещения.
                 * Состав совпадает с закупкой. Пользователь может отредактировать состав и перейти к Итоговой странице.
                 */
                progressDialog.dismiss();
                open(saleModel, SaleMode.DISPLACEMENT);
                break;
            case SURCHARGE:
                /**
                 * Переходим к Итоговой странице для выбора формы оплаты.
                 * Суммы проставляем заранее автоматически.
                 * Пользователь может перейти к редактированию как информации о поставщике,
                 * так и составу заказу (через доп.обращение к серверу).
                 */
                progressDialog.dismiss();
                OrderActivity.start(view.getContext());

                break;
            case REMOVE:
                removeOrder(saleModel);
                progressDialog.dismiss();
                break;
            case EXTRA_INFORMATION:
            case SELL:
                /**
                 * Показываем Итоговый экран, где автоматически выставляем сумму оплаты в рублях.
                 */

                EffectFastActionActivity.Data data = EffectFastActionActivity.Data
                        .builder()
                        .docType(saleModel.getDocType())
                        .fastActionType(orderAction.getId())
                        .sessionId(sessionId)
                        .storage1CId(currentSalePnt.getStorage1CId())
                        .orderDate(saleModel.getOrderGlance().getOrder1CDate())
                        .order1CId(saleModel.getOrderGlance().getOrder1CId())
                        .build();

                EffectFastActionActivity fastActionActivity = EffectFastActionActivity.create(data);

                Subscription subscribe = dataController.getOrderDetailedData(fastActionActivity)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnEach(notification -> progressDialog.dismiss())
                        .subscribe(orderDetailedData -> {
                            OrderActivity.start(view.getContext(),
                                    SaleMode.SALE,
                                    String.valueOf(orderDetailedData.getOrderTotalSum()),
                                    String.valueOf(orderDetailedData.getOrderTotalSum()),
                                    orderDetailedData.convertGoodsToOld(),
                                    orderDetailedData, 0, 0);
                        }, view::showError);
                this.subscription.add(subscribe);
                break;
            case COPY:
                Subscription copySubscription = getLoadDetailedOrderObserbable(saleModel)
                        .ofType(OrderDetailed.class)
                        .flatMap(orderDetailed -> {
                            orderDetailed.setOrder1CId("");
                            orderDetailed.setOrderDate(Utils.currentDateUTC());
                            AddUpdateOrderPhotoAuth addUpdateOrderPhotoAuth = AddUpdateOrderPhotoAuth.create(sessionId, currentSalePnt.getStorage1CId(), orderDetailed);
                            return dataController.addUpdateOrderPhotoAuth(addUpdateOrderPhotoAuth)
                                    .subscribeOn(Schedulers.io())
                                    .map(addUpdateOrderResponse -> orderDetailed);

                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnEach(notification -> {
                            progressDialog.dismiss();
                        })
                        .subscribeOn(Schedulers.io())
                        .subscribe(order -> {
                            SaleCompositionActivity.start(context, order, SaleMode.SALE, orderAction);
                        }, view::showError);
                this.subscription.add(copySubscription);
                break;
            default:
                Utils.toast(view.getContext(), "Действие " + orderAction.name() + " еще  не обрабатывается в методе selectedActionForSaleModel ");
        }
    }

    @Override
    public void removeOrder(SaleModel saleModel) {
        Subscription subscription = Observable.just(null)
                .subscribe(o -> {
                    updateSubject.onNext(updateSubject.getValue() + 1);
                });

        this.subscription.add(subscription);
    }

    @Override
    public void updateComment(String comment, SaleModel saleModel1) {
        Subscription subscription = getLoadDetailedOrderObserbable(saleModel1)
                .ofType(OrderDetailed.class)
                .flatMap(orderDetailed -> {
                    orderDetailed.setCommentString(comment);
                    orderDetailed.setOrderDate(Utils.currentDateUTC());
                    AddUpdateOrderPhotoAuth addUpdateOrderPhotoAuth = AddUpdateOrderPhotoAuth.create(sessionId, currentSalePnt.getStorage1CId(), orderDetailed);
                    return dataController.addUpdateOrderPhotoAuth(addUpdateOrderPhotoAuth)
                            .subscribeOn(Schedulers.io())
                            .map(addUpdateOrderResponse -> orderDetailed);

                })
                .subscribe(orderDetailed -> {
                    view.showUpdatedOrderDetailed(comment, saleModel1.getOrderGlance().getOrder1CId());
                }, Error.onError());
        this.subscription.add(subscription);
    }

    @Override
    public void updateGoodsFast() {
        view.showProgressFastUpdate();
        subscription.add(fastUpdateObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(fastUpdate -> view.stopProgressFastUpdate())
                .subscribe(fastUpdate -> view.stopProgressFastUpdate(), view::showError));
    }

    private Observable<FastUpdate> fastUpdateObservable() {
        return dataController.updateGoodsFast(sessionId, currentSalePnt.getStorage1CId())
                .subscribeOn(Schedulers.io());
    }

    @Override
    public void onUpdate() {
        if (subscription.hasSubscriptions() && !subscription.isUnsubscribed()) {
            updateSubject.onNext(1);
        } else {
            start();
        }
    }
}
