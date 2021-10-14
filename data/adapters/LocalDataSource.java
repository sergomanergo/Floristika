package ru.kazachkov.florist.data.adapters;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import lombok.Getter;
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
import ru.kazachkov.florist.api.model.request.AddUpdateOrderPhotoAuth;
import ru.kazachkov.florist.api.model.request.EffectFastActionActivity;
import ru.kazachkov.florist.api.model.request.GetCompaniesListAuth;
import ru.kazachkov.florist.api.model.request.GetUsersAtWork;
import ru.kazachkov.florist.api.model.response.AddUpdateOrder2SellerResponse;
import ru.kazachkov.florist.api.model.response.AddUpdateOrderResponse;
import ru.kazachkov.florist.api.model.response.AddUpdateOrderSalesAuthResponse;
import ru.kazachkov.florist.api.realm.RealmCategory;
import ru.kazachkov.florist.api.realm.RealmCompany;
import ru.kazachkov.florist.api.realm.RealmEdIzm;
import ru.kazachkov.florist.api.realm.RealmFakeItem;
import ru.kazachkov.florist.api.realm.RealmItem;
import ru.kazachkov.florist.api.realm.RealmItemCategory;
import ru.kazachkov.florist.api.realm.RealmItemPrices;
import ru.kazachkov.florist.api.realm.RealmOrderFastActions;
import ru.kazachkov.florist.api.realm.RealmPhoto;
import ru.kazachkov.florist.api.realm.RealmStock;
import ru.kazachkov.florist.data.DataSource;
import ru.kazachkov.florist.interfaces.FromRealmConvert;
import ru.kazachkov.florist.interfaces.OrderInfo;
import ru.kazachkov.florist.interfaces.OrdersRequestData;
import ru.kazachkov.florist.interfaces.ToRealmConvert;
import ru.kazachkov.florist.model.GoodsModel;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.functions.Func1;


public class LocalDataSource implements DataSource.Local {

    @Getter
    private final Realm realm;

    public LocalDataSource(Realm realm) {
        this.realm = realm;
    }

    @Override
    public Observable<List<Category>> getGroupsObs() {
        return realm.where(RealmCategory.class)
                .findAllAsync()
                .asObservable()
                .map(this::convert);
    }

    @Override
    public Observable<List<CompanyAtGlanceDT>> getCompaniesList(GetCompaniesListAuth getCompaniesListAuth, Boolean fromRemote) {

        return Observable.just(true).
                subscribeOn(AndroidSchedulers.mainThread()).
                flatMap( map ->
                    realm.where(RealmCompany.class)
                            .findAll()
                            .asObservable()
                            .map(this::convert)
                            .flatMap(mp ->
                                    Observable.from(mp).toList())

                );
    }

    @Override
    public Observable<List<User1CIdDT>> getUsersList(GetUsersAtWork item) {
        return null;
    }

    @Override
    public Observable<List<EdIzm>> getUnits() {
        return realm.where(RealmEdIzm.class)
                .findAll()
                .asObservable()
                .map(this::convert);
    }

    @Override
    public Observable<OrderInfo> getOrders(OrdersRequestData orderRequestData) {
        return Observable.error(new NullPointerException("LocalDataSource not implement this method"));
    }

    @Override
    public Observable<AddUpdateOrderResponse> addUpdateOrderPhotoAuth(AddUpdateOrderPhotoAuth addUpdateOrderPhotoAuth) {
        return Observable.error(new NullPointerException("LocalDataSource not implement this method"));
    }

    private <R extends RealmObject & FromRealmConvert<T>, T> List<T> convert(RealmResults<R> realmItems) {
        List<T> items = new ArrayList<>();
        for (R item : realmItems) {
            items.add(item.fromRealm());
        }
        return items;
    }

    @Override
    public Observable<List<GoodsModel>> getGoodsModelsList(String storageId) {
        return realm.where(RealmItem.class)
                .findAll()
                .asObservable()
                .flatMap(realmItems -> Observable.from(realmItems).map(RealmItem::fromRealm)
                        .map(item ->
                                new GoodsModel(item,
                                        getItemPrices(item.getFlower1CId()),
                                        getPhotos(item.getFlower1CId()),
                                        getEdIzm(item.getEdIzmId()),
                                        getCategories(item.getFlower1CId()),
                                        getStock(storageId, item.getFlower1CId())))
                        .toList());
    }

    @Override
    public Observable<GoodsModel> getGoodsModel(String flower1CId, String storageId) {
        return realm.where(RealmItem.class)
                .equalTo("flower1CId", flower1CId)
                .findFirst()
                .<RealmItem>asObservable()
                .first()
                .map(RealmItem::fromRealm)
                .map(item -> new GoodsModel(item,
                        getItemPrices(item.getFlower1CId()),
                        getPhotos(item.getFlower1CId()),
                        getEdIzm(item.getEdIzmId()),
                        getCategories(item.getFlower1CId()),
                        getStock(storageId, item.getFlower1CId())));
    }

    @Override
    public Observable<OrderFastActions> getOrderFastActions(int type) {
        return realm.where(RealmOrderFastActions.class)
                .equalTo("typeId", type)
                .findFirst()
                .<RealmOrderFastActions>asObservable()
                .map(RealmOrderFastActions::fromRealm);
    }

    @Override
    public Observable<List<Category>> getCategories() {
        return realm.where(RealmCategory.class)
                .findAllAsync()
                .asObservable()
                .filter(RealmResults::isLoaded)
                .flatMap(new Func1<RealmResults<RealmCategory>, Observable<List<Category>>>() {
                    @Override
                    public Observable<List<Category>> call(RealmResults<RealmCategory> realmCategories) {
                        return Observable.from(realmCategories).map(RealmCategory::fromRealm).toList();
                    }
                });
    }


    @Override
    public Observable<Object> saveOrderPhotos(List<String> photosUri, String orderId) {
        return Observable.from(photosUri)
                .map(s -> new Photo(orderId, s).convertToFunc())
                .toList()
                .flatMap(realmObjects -> Observable.create(subscriber -> {
                    realm.executeTransactionAsync(realm1 -> realm1.copyToRealmOrUpdate(realmObjects),
                            () -> subscriber.onNext(Observable.just(true)), subscriber::onError);
                }));
    }

    @Override
    public Observable<List<Photo>> getOrderPhotos(String orderId) {
        return realm.where(RealmPhoto.class)
                .equalTo("itemId", orderId)
                .findAllAsync()
                .asObservable()
                .filter(RealmResults::isLoaded)
                .flatMap(new Func1<RealmResults<RealmPhoto>, Observable<List<Photo>>>() {
                    @Override
                    public Observable<List<Photo>> call(RealmResults<RealmPhoto> realmPhotos) {
                        return Observable.from(realmPhotos).map(RealmPhoto::fromRealm).toList();
                    }
                });
    }

    @Override
    public Observable<AddUpdateOrder2SellerResponse> addUpdateOrder2PhotoAuth(String sessionId, String storageId, Order2Seller order2Seller) {
        return null;
    }

    @Override
    public Observable<AddUpdateOrderSalesAuthResponse> addUpdateOrderSalesAuth(String sessionId, String storageId, OrderData2Save orderData2Save) {
        return null;
    }


    private Stock getStock(String storage1CId, String flower1CId) {
        return realm.where(RealmStock.class)
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

    @Override
    public Observable<FastUpdate> saveFastUpdate(FastUpdate fastUpdate) {
        return Observable.defer(new Func0<Observable<FastUpdate>>() {
            @Override
            public Observable<FastUpdate> call() {
                return Observable.from(fastUpdate.toRealmConverts())
                        .map(ToRealmConvert::convertToFunc)
                        .toList()
                        .flatMap(realmObjects -> Observable.create(subscriber -> {
                            realm.executeTransactionAsync(realm1 -> realm1.copyToRealmOrUpdate(realmObjects), () -> {
                                subscriber.onNext(fastUpdate);
                                subscriber.onCompleted();
                            }, subscriber::onError);
                        }));
            }
        });
    }

    @Override
    public Observable<OrderDetailedData4> getOrderDetailedData(EffectFastActionActivity fastActionActivity) {
        return null;
    }

    @Override
    public Observable<List<CompanyAtGlanceDT>> saveCompanies(List<CompanyAtGlanceDT> companyAtGlanceDTs1) {

        return Observable.from(companyAtGlanceDTs1)
                .map(CompanyAtGlanceDT::convertToFunc)
                .toList()
                .flatMap(realmObjects ->
                        {
                            return Observable.create(subscriber -> {

                                getRealm().executeTransactionAsync(realm -> realm.where(RealmCompany.class).findAll().deleteAllFromRealm(), () -> {
                                    subscriber.onNext(realmObjects);//
                                    subscriber.onCompleted();
                                }, error -> {
                                            error.printStackTrace();
                                        }
                                );
                            });
                        })
                .map(map ->
                        (List<RealmCompany>)map )
                .flatMap(realmObjects2 -> {
                    return Observable.create(subscriber2 -> {
                        getRealm().executeTransactionAsync(realm1 ->
                                realm1.copyToRealmOrUpdate(realmObjects2), () -> {
                            subscriber2.onNext(companyAtGlanceDTs1);
                            subscriber2.onCompleted();
                        }, subscriber2::onError);
                    });
                });
    }

    @Override
    public String getRealmFakeItem() {
        return getRealm().where(RealmFakeItem.class).findFirst().fromRealm();
    }
}
