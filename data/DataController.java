package ru.kazachkov.florist.data;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import lombok.Getter;
import okhttp3.ResponseBody;
import ru.kazachkov.florist.api.FloristApi;
import ru.kazachkov.florist.api.model.Category;
import ru.kazachkov.florist.api.model.CompanyAtGlanceDT;
import ru.kazachkov.florist.api.model.EdIzm;
import ru.kazachkov.florist.api.model.FastUpdate;
import ru.kazachkov.florist.api.model.ItemPrices;
import ru.kazachkov.florist.api.model.Order2Seller;
import ru.kazachkov.florist.api.model.OrderData2Save;
import ru.kazachkov.florist.api.model.OrderDetailedData4;
import ru.kazachkov.florist.api.model.OrderFastActions;
import ru.kazachkov.florist.api.model.Photo;
import ru.kazachkov.florist.api.model.Stock;
import ru.kazachkov.florist.api.model.User1CIdDT;
import ru.kazachkov.florist.api.model.UserAuthRole;
import ru.kazachkov.florist.api.model.request.AddUpdateGoodItemAuthJS;
import ru.kazachkov.florist.api.model.request.AddUpdateOrderPhotoAuth;
import ru.kazachkov.florist.api.model.request.DeactivateSessionJS;
import ru.kazachkov.florist.api.model.request.EffectFastActionActivity;
import ru.kazachkov.florist.api.model.request.GetCompaniesListAuth;
import ru.kazachkov.florist.api.model.request.GetOrderListAuth;
import ru.kazachkov.florist.api.model.request.GetOrderSellerListAuth;
import ru.kazachkov.florist.api.model.request.GetUsersAtWork;
import ru.kazachkov.florist.api.model.request.SessionPassword;
import ru.kazachkov.florist.api.model.request.UpdateGoodsFastAuthJS;
import ru.kazachkov.florist.api.model.response.AddUpdateOrder2SellerResponse;
import ru.kazachkov.florist.api.model.response.AddUpdateOrderResponse;
import ru.kazachkov.florist.api.model.response.AddUpdateOrderSalesAuthResponse;
import ru.kazachkov.florist.api.model.response.CompaniesList;
import ru.kazachkov.florist.api.model.response.ItemDataJS;
import ru.kazachkov.florist.api.model.response.OrderList;
import ru.kazachkov.florist.api.model.response.OrdersListAction;
import ru.kazachkov.florist.api.model.response.UsersList;
import ru.kazachkov.florist.api.realm.RealmCategory;
import ru.kazachkov.florist.api.realm.RealmEdIzm;
import ru.kazachkov.florist.api.realm.RealmItem;
import ru.kazachkov.florist.api.realm.RealmItemCategory;
import ru.kazachkov.florist.api.realm.RealmItemPrices;
import ru.kazachkov.florist.api.realm.RealmOrderListAction;
import ru.kazachkov.florist.api.realm.RealmPhoto;
import ru.kazachkov.florist.api.realm.RealmSalePnt;
import ru.kazachkov.florist.api.realm.RealmStock;
import ru.kazachkov.florist.data.adapters.LocalDataSource;
import ru.kazachkov.florist.data.adapters.RemoteDataSource;
import ru.kazachkov.florist.interfaces.FromRealmConvert;
import ru.kazachkov.florist.interfaces.IOrderGlance;
import ru.kazachkov.florist.interfaces.OrderInfo;
import ru.kazachkov.florist.interfaces.OrdersRequestData;
import ru.kazachkov.florist.interfaces.PotentialErrorsProvider;
import ru.kazachkov.florist.interfaces.ToRealmConvert;
import ru.kazachkov.florist.model.GoodsModel;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;


/**
 * Created by ishmukhametov on 23.03.16.
 * The DataController provides the data.
 */
public class DataController implements DataSource.Common {

    private final RemoteDataSource remoteDataSource;
    private final LocalDataSource localDataSource;
    private int control;

    @Deprecated
    public FloristApi getFloristApi() {
        return remoteDataSource.getFloristApi();
    }

    @Deprecated
    public Realm getRealm() {
        return localDataSource.getRealm();
    }

    @Getter
    private BehaviorSubject<OrderList.OrdersReportData> orderListSubject = BehaviorSubject.create();

    public DataController(RemoteDataSource remoteDataSource, LocalDataSource localDataSource) {
        this.remoteDataSource = remoteDataSource;
        this.localDataSource = localDataSource;
    }

    public int getControl() {
        return control;
    }

    public void setControl(int control) {
        this.control = control;
    }


    private Observable<OrderInfo> getOrderListObs(GetOrderListAuth requestBody) {
        return fetchOrderListAuth(requestBody)
                .map(OrderList::getOrdersReportData)
                .map(ordersReportData -> {
                    List<IOrderGlance> orderLists = ordersReportData.getOrderList();
                    int i = 0;
                    for (IOrderGlance item : orderLists) item.setPosition(i++);
                    return ordersReportData;
                });
    }


    public Observable<UserAuthRole> auth(SessionPassword sessionPassword) {
        return getFloristApi().checkSessionPasswordJS4(sessionPassword);
    }

    public Observable<List<OrdersListAction>> getOrderListActions() {
        return getRealm().where(RealmOrderListAction.class)
                .findAllSortedAsync("orderListSequence")
                .asObservable()
                .map(realmSalePnts -> {
                    List<OrdersListAction> actions = new ArrayList<>();
                    for (RealmOrderListAction item : realmSalePnts) {
                        actions.add(item.fromRealm());
                    }
                    return actions;
                }).cache();
    }


    private Observable<OrderList> fetchOrderListAuth(GetOrderListAuth getOrderListAuth) {
        return getFloristApi().getOrderListAuth(getOrderListAuth)
                .flatMap(errorHandle());
    }

    @Override
    public Observable<AddUpdateOrderResponse> addUpdateOrderPhotoAuth(AddUpdateOrderPhotoAuth addUpdateOrderPhotoAuth) {
        return remoteDataSource.addUpdateOrderPhotoAuth(addUpdateOrderPhotoAuth);
    }

    public Observable<ItemDataJS> addUpdateGoodItemAuth(AddUpdateGoodItemAuthJS item) {
        return getFloristApi().addUpdateGoodItemAuth(item);
    }

    public Observable<UsersList> getUsersAtWork(GetUsersAtWork item) {
        return getFloristApi().getUsersAtWork(item)
                .flatMap(errorHandle());
    }

    public Observable<ResponseBody> deactivateSessionJS(String sessionId) {
        return getFloristApi().deactivateSessionJS(DeactivateSessionJS.create(sessionId));
    }

    private <T extends PotentialErrorsProvider> Func1<T, Observable<T>> errorHandle() {
        return result -> {
            if (result.getAuthResult().isSuccessful()) {
                return Observable.just(result);
            }
            return Observable.error(new Throwable(result.getAuthResult().getMsgValue()));
        };
    }

    @Deprecated
    public Observable<List<CompanyAtGlanceDT>> getCompaniesListAuth(GetCompaniesListAuth getCompaniesListAuth) {
        return getFloristApi().getCompaniesListAuth(getCompaniesListAuth)
                .map(CompaniesList::getOrdersReportData);
    }

    public Observable<List<UserAuthRole.SalePnt>> getSalePt() {
        return getRealm().where(RealmSalePnt.class)
                .findAllAsync()
                .asObservable()
                .map(realmSalePnts -> {
                    List<UserAuthRole.SalePnt> salePts = new ArrayList<>();
                    for (RealmSalePnt item : realmSalePnts) {
                        salePts.add(item.fromRealm());
                    }
                    return salePts;
                });
    }


    public <T extends ToRealmConvert> Observable<Boolean> saveToRealmObs(List<T> t) {
        return Observable.from(t)
                .map(ToRealmConvert::convertToFunc)
                .toList()
                .switchMap(realmObjects -> Observable.create(new Observable.OnSubscribe<Boolean>() {
                    @Override
                    public void call(Subscriber<? super Boolean> subscriber) {
                        getRealm().executeTransactionAsync(realm1 -> realm1.copyToRealmOrUpdate(realmObjects),
                                () -> subscriber.onNext(true),
                                subscriber::onError);
                    }
                }));
    }


    public Observable<GoodsModel> getGoodsModels(String storageId) {
        return getRealm().where(RealmItem.class)
                .findAll()
                .asObservable()
                .flatMap(Observable::from)
                .map(RealmItem::fromRealm)
                .map(item ->
                        new GoodsModel(item,
                                getItemPrices(item.getFlower1CId()),
                                getPhotos(item.getFlower1CId()),
                                getEdIzm(item.getEdIzmId()),
                                getCategories(item.getFlower1CId()),
                                getStock(storageId, item.getFlower1CId())));
    }

    public Observable<List<GoodsModel>> getGoodsModelsList(String storageId) {
        return localDataSource.getGoodsModelsList(storageId);
    }

    @Override
    public Observable<GoodsModel> getGoodsModel(String flowerId, String storageId) {
        return localDataSource.getGoodsModel(flowerId, storageId);
    }

    @Override
    public Observable<FastUpdate> saveFastUpdate(FastUpdate fastUpdate) {
        return localDataSource.saveFastUpdate(fastUpdate);
    }

    @Override
    public Observable<OrderFastActions> getOrderFastActions(int type) {
        return localDataSource.getOrderFastActions(type);
    }

    private Stock getStock(String storage1CId, String flower1CId) {
        return getRealm().where(RealmStock.class)
                .equalTo("storage1CId", storage1CId)
                .equalTo("flower1CId", flower1CId)
                .findFirst()
                .fromRealm();
    }

    private List<Category> getCategories(String flower1CId) {
        List<RealmItemCategory> realmItemCategories = getRealm().where(RealmItemCategory.class).equalTo("flower1CId", flower1CId).findAll();
        List<Category> categories = new ArrayList<>();
        for (RealmItemCategory realmIC : realmItemCategories) {
            Category cat = getRealm().where(RealmCategory.class).equalTo("category1CId", realmIC.getCategory1CId()).findFirst().fromRealm();
            categories.add(cat);
        }
        return categories;
    }

    @Override
    public Observable<List<EdIzm>> getUnits() {
        return localDataSource.getUnits();
    }

    @Override
    public Observable<List<Category>> getGroupsObs() {
        return localDataSource.getGroupsObs();
    }


    private ItemPrices getItemPrices(String id) {
        return getRealm().where(RealmItemPrices.class)
                .equalTo("flower1CId", id)
                .findFirst()
                .fromRealm();
    }

    private EdIzm getEdIzm(String id) {
        RealmEdIzm realmEdIzm;
        if (TextUtils.isEmpty(id)) {
            realmEdIzm = getRealm().where(RealmEdIzm.class).findFirst();
        } else {
            realmEdIzm = getRealm().where(RealmEdIzm.class)
                    .equalTo("edIzm1CId", id)
                    .findFirst();
        }
        if (realmEdIzm != null) return realmEdIzm.fromRealm();
        return null;
    }

    public List<Photo> getPhotos(String id) {
        return convert(getRealm().where(RealmPhoto.class)
                .equalTo("itemId", id)
                .findAll());
    }


    private <R extends RealmObject & FromRealmConvert<T>, T> List<T> convert(RealmResults<R> realmItems) {
        List<T> items = new ArrayList<>();
        for (R item : realmItems) {
            items.add(item.fromRealm());
        }
        return items;
    }


    @Override
    public Observable<List<CompanyAtGlanceDT>> getCompaniesList(GetCompaniesListAuth getCompaniesListAuth, Boolean fromRemote) {

        if(fromRemote) {
            return remoteDataSource.getCompaniesList(getCompaniesListAuth, true)
                    .flatMap(companyAtGlanceDTs ->
                            Observable.from(companyAtGlanceDTs).map(companyAtGlanceDT -> {
                                companyAtGlanceDT.setStorageId(getCompaniesListAuth.getBody().getData().getStorageId());
                                return companyAtGlanceDT;
                            }))
                    .toList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap( map -> {
                        return localDataSource.saveCompanies(map);
                    } )
                    ;
        } else
            return// Observable.concat(
                     localDataSource.getCompaniesList(getCompaniesListAuth,false);
                    /*         , this.getCompaniesList(getCompaniesListAuth, true)
            )*/

               //.filter(lists -> !lists.isEmpty());





                /*Observable.concat(,
                                  remoteDataSource.getCompaniesList(getCompaniesListAuth, true)
                                          .flatMap(companyAtGlanceDTs ->
                                                  Observable.from(companyAtGlanceDTs).map(companyAtGlanceDT -> {
                                                      companyAtGlanceDT.setStorageId(getCompaniesListAuth.getBody().getData().getStorageId());
                                                      return companyAtGlanceDT;
                                                  }).toList().flatMap(
                                                          localDataSource::saveCompanies))
                )
                        .first(dataList ->
                                !dataList.isEmpty());*/

               //localDataSource.getCompaniesList(getCompaniesListAuth,false)
                /*.flatMap(companyAtGlanceDTs -> {
                    boolean empty = companyAtGlanceDTs.isEmpty();
                    if (empty) return Observable.empty();
                    return Observable.just(companyAtGlanceDTs);
                })*/

               /* .flatMap(companyAtGlanceDTs ->
                        Observable.from(companyAtGlanceDTs).map(companyAtGlanceDT -> {
                    companyAtGlanceDT.setStorageId(getCompaniesListAuth.getBody().getData().getStorageId());
                    return companyAtGlanceDT;
                }).toList().flatMap(
                        localDataSource::saveCompanies))
                        */

    }

    @Override
    public Observable<List<User1CIdDT>> getUsersList(GetUsersAtWork item) {
        return remoteDataSource.getUsersList(item);
    }

    @Override
    public Observable<OrderInfo> getOrders(OrdersRequestData orderRequestData) {
        if (orderRequestData instanceof GetOrderSellerListAuth) {
            return remoteDataSource.getOrders(orderRequestData);
        } else if (orderRequestData instanceof GetOrderListAuth) {
            return getOrderListObs((GetOrderListAuth) orderRequestData);
        }
        return Observable.error(new IllegalArgumentException("OrdersRequestData instance of GetOrderSellerListAuth|GetOrderListAuth???"));
    }

    @Override
    public Observable<List<Category>> getCategories() {
        return localDataSource.getCategories();
    }


    @Override
    public Observable<List<Photo>> getOrderPhotos(String orderId) {
        return localDataSource.getOrderPhotos(orderId);
    }

    @Override
    public Observable<Object> saveOrderPhotos(List<String> photosUri, String orderId) {
        return localDataSource.saveOrderPhotos(photosUri, orderId);
    }

    @Override
    public Observable<FastUpdate> updateGoodsFast(String sessionId, String storageId) {
        return remoteDataSource.updateGoodsFast(UpdateGoodsFastAuthJS.create(sessionId, storageId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(fastUpdateAuth -> {
                    if (fastUpdateAuth.getAuthResult().isSuccessful()) {
                        return localDataSource.saveFastUpdate(fastUpdateAuth.getFastUpdate());
                    } else {
                        return Observable.error(new IllegalArgumentException(fastUpdateAuth.getAuthResult().getMsgValue()));
                    }
                });
    }

    @Override
    public Observable<AddUpdateOrder2SellerResponse> addUpdateOrder2PhotoAuth(String sessionId, String storageId, Order2Seller order2Seller) {
        return remoteDataSource.addUpdateOrder2PhotoAuth(sessionId, storageId, order2Seller);
    }

    @Override
    public Observable<AddUpdateOrderSalesAuthResponse> addUpdateOrderSalesAuth(String sessionId, String storageId, OrderData2Save orderData2Save) {
        return remoteDataSource.addUpdateOrderSalesAuth(sessionId, storageId, orderData2Save);
    }

    @Override
    public Observable<OrderDetailedData4> getOrderDetailedData(EffectFastActionActivity fastActionActivity) {
        return remoteDataSource.getOrderDetailedData(fastActionActivity).subscribeOn(Schedulers.io());
    }

    public String getRealmFakeItem() {
        return localDataSource.getRealmFakeItem();
    }
}
