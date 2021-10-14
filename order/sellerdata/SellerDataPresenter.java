package ru.kazachkov.florist.order.sellerdata;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

import ru.kazachkov.florist.api.model.Category;
import ru.kazachkov.florist.api.model.CompanyAtGlanceDT;
import ru.kazachkov.florist.api.model.User1CIdDT;
import ru.kazachkov.florist.api.model.request.GetCompaniesListAuth;
import ru.kazachkov.florist.api.model.request.GetUsersAtWork;
import ru.kazachkov.florist.app.AppPreferences;
import ru.kazachkov.florist.data.DataController;
import ru.kazachkov.florist.data.adapters.LocalDataSource;
import ru.kazachkov.florist.interfaces.OrderData;
import ru.kazachkov.florist.model.Client;
import ru.kazachkov.florist.tools.SaleMode;
import ru.kazachkov.florist.tools.SchedulersProvider;
import ru.kazachkov.florist.tools.Utils;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;

import static ru.kazachkov.florist.tools.Preconditions.checkNotNull;

public class SellerDataPresenter implements SellerDataContract.Presenter {

    private final DataController dataController;

    private final SellerDataContract.View view;

    private final CompositeSubscription subscriptions;
    private final AppPreferences appPreferences;
    private final SchedulersProvider schedulersProvider;
    private List<CompanyAtGlanceDT> companyAtGlanceDTs;
    private List<User1CIdDT> users;
    private BigDecimal basePrice;
    private User1CIdDT user1CId;
    private String bayer1CId;
    private CompanyAtGlanceDT currentClient;
    private Category category;
    private Activity activity;

    private BehaviorSubject<String> initClientSubject = BehaviorSubject.create();
    private BehaviorSubject<String> initAuthorSubject = BehaviorSubject.create();

    public SellerDataPresenter(@NonNull Activity activity,
                               @NonNull DataController dataController,
                               @NonNull SellerDataContract.View view,
                               @NonNull AppPreferences appPreferences,
                               SchedulersProvider schedulersProvider) {
        this.dataController = checkNotNull(dataController);
        this.view = checkNotNull(view);
        this.activity = activity;
        this.appPreferences = appPreferences;
        this.schedulersProvider = schedulersProvider;
        subscriptions = new CompositeSubscription();
        view.setPresenter(this);
    }

    public void setBasePrice(String basePrice) {
        this.basePrice = new BigDecimal(basePrice);
    }

    public void refreshSubscriptions(Boolean fromRemote) {

        unsubscribe();
        Observable<List<CompanyAtGlanceDT>> companiesList = dataController.getCompaniesList(GetCompaniesListAuth.create(appPreferences.getSessionId(), appPreferences.getSalePntId()), fromRemote).publish().refCount();

        Subscription companiesSubscription = companiesList
                .subscribeOn(schedulersProvider.getIo())
                .observeOn(schedulersProvider.getMain())
                .doOnNext(companyAtGlanceDTs -> {
                    SellerDataPresenter.this.companyAtGlanceDTs = companyAtGlanceDTs;
                    if(companyAtGlanceDTs.size() == 0) {
                        refreshSubscriptions(true);
                    }
                })
                .first(companyAtGlanceDTs1 -> companyAtGlanceDTs1.size() != 0)
                .flatMap(companyAtGlanceDTs12 -> Observable.just(companyAtGlanceDTs12).zipWith(initClientSubject, Pair::create)
                        .flatMap(listStringPair -> {
                            if (TextUtils.isEmpty(listStringPair.second))
                                return Observable.from(listStringPair.first).filter(companyAtGlanceDT -> companyAtGlanceDT.getIsBuyer()==1).first();
                            return Observable.from(listStringPair.first).filter(companyAtGlanceDT -> companyAtGlanceDT.getIsBuyer()==1)
                                    .firstOrDefault(new CompanyAtGlanceDT(listStringPair.second, "ОТСУТВУЕТ В СПИСКЕ", 1, 1,"","",0.0,0.0,"", appPreferences.getSalePntId()), companyAtGlanceDT -> companyAtGlanceDT.getCompany1CId().equals(listStringPair.second));
                        }))
                .subscribe(client -> {
                    currentClient = client;
                    view.showFirstCompanyInSellerData(client);
                }, throwable -> view.showErrorLoadingCompanies());
       /* if((companyAtGlanceDTs == null || companyAtGlanceDTs.size() == 0) && !fromRemote) {
            refreshSubscriptions(true);
            return;
        }*/

        Observable<List<CompanyAtGlanceDT>> clientsSearchObs = Observable.combineLatest(companiesList
                        .subscribeOn(schedulersProvider.getIo())
                , view.getSearchObs(), Pair::create)
             //   .observeOn(schedulersProvider.getIo())
                .flatMap(new Func1<Pair<List<CompanyAtGlanceDT>, CharSequence>, Observable<List<CompanyAtGlanceDT>>>() {
                    @Override
                    public Observable<List<CompanyAtGlanceDT>> call(Pair<List<CompanyAtGlanceDT>, CharSequence> listStringPair) {
                        return Observable.from(listStringPair.first)
                                .filter(companyAtGlanceDT -> companyAtGlanceDT.getIsBuyer()==1 &&
                                        (companyAtGlanceDT.getCompanyName().toLowerCase().contains(listStringPair.second.toString().toLowerCase())
                                      || (companyAtGlanceDT.getTelNumber() != null && companyAtGlanceDT.getTelNumber().contains(listStringPair.second.toString()) )
                                      || TextUtils.isEmpty(listStringPair.second)))
                                .toList()
                                ;
                    }
                });

        subscriptions.add(clientsSearchObs
                .observeOn(schedulersProvider.getMain())
                .subscribe(view::showClients, view::showError));


        GetUsersAtWork item = GetUsersAtWork.create(
                appPreferences.getSessionId(),
                appPreferences.getSalePntId(),
                Utils.currentDateUTC(),
                -1);

        Subscription usersSubscription = dataController.getUsersList(item)
                .subscribeOn(schedulersProvider.getIo())
                .observeOn(schedulersProvider.getMain())
                .zipWith(initAuthorSubject, Pair::create)
                .flatMap(listStringPair -> {
                    if (TextUtils.isEmpty(listStringPair.second)) {
                        return Observable.just(Pair.create(listStringPair.first, 0));
                    } else {
                        return Observable.from(listStringPair.first)
                                .takeWhile(user1CIdDT -> !user1CIdDT.getUser1CId().equals(listStringPair.second))
                                .count()
                                .map(integer -> Pair.create(listStringPair.first, integer));
                    }
                })
                .subscribe(pair -> {
                    SellerDataPresenter.this.users = pair.first;
                    view.showAuthors(users, pair.second);
                }, throwable -> {
                    view.showErrorLoadingCompanies();
                });

       /* Observable<List<Category>> categoryObservable = dataController.getCategories();

        categoryObservable
                .subscribe(view::showCategories, view::showError);*/
        view.showCategories(getSimpleCategories());

        subscriptions.add(companiesSubscription);
        subscriptions.add(usersSubscription);

    }

    @Override
    public void subscribe() {
        refreshSubscriptions(false);
    }

    public List<Category> getSimpleCategories() {
        return Arrays.asList(
                new Category("0", "Обычный"),
                new Category("1", "Горшечные")
        );
    }

    @Override
    public void unsubscribe() {
        subscriptions.clear();
    }

    @Override
    public void accountingChanged(boolean isChecked) {

    }

    @Override
    public void addNewClient() {
        view.showAddClientDialog();
    }

    @Override
    public void setSaleMode(SaleMode saleMode) {

    }


    @Override
    public List<User1CIdDT> getAuthors() {
        return users;
    }

    @Override
    public void setSelectedAuthorPosition(int i) {

    }

    @Override
    public void setSelectedResponsiblePosition(int i) {

    }

    @Override
    public String getBasePrice() {
        if (basePrice == null) throw new NullPointerException("no set base price");
        return basePrice.setScale(0, RoundingMode.HALF_UP).toString();
    }

    @Override
    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    @Override
    public String getAuthorId() {
        return user1CId.getUser1CId();
    }

    @Override
    public String getCategoryId() {
        return category.getCategory1CId();
    }

    @Override
    public void selectClient(CompanyAtGlanceDT client) {
        view.showClientInfo(client);
        currentClient = client;
    }

    @Override
    public void setSelectedAuthor(User1CIdDT item) {
        this.user1CId = item;
    }

    @Override
    public void stopSearch() {

    }

    @Override
    public void startSearch() {
        view.showSearchViewWithText(currentClient);
    }

    @Override
    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public void init(OrderData orderData) {
        if (orderData != null) {
            initClientSubject.onNext(orderData.getBuyer1CId());
            initAuthorSubject.onNext("");
            //initAuthorSubject.onNext(orderData.getUser1CId());
        } else {
            initClientSubject.onNext("");
            initAuthorSubject.onNext("");
        }
    }

    @Override
    public String getClient1cId() {
        return currentClient.getCompany1CId();
    }

    @Override
    public void clientResult(Client client) {
        //todo
    }
}
