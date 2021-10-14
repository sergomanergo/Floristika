package ru.kazachkov.florist.data.adapters;

import java.util.List;

import lombok.Getter;
import ru.kazachkov.florist.api.FloristApi;
import ru.kazachkov.florist.api.model.AuthResult;
import ru.kazachkov.florist.api.model.Category;
import ru.kazachkov.florist.api.model.CompanyAtGlanceDT;
import ru.kazachkov.florist.api.model.EdIzm;
import ru.kazachkov.florist.api.model.FastUpdate;
import ru.kazachkov.florist.api.model.Order2Seller;
import ru.kazachkov.florist.api.model.OrderData2Save;
import ru.kazachkov.florist.api.model.OrderDetailedData4;
import ru.kazachkov.florist.api.model.OrderFastActions;
import ru.kazachkov.florist.api.model.Photo;
import ru.kazachkov.florist.api.model.User1CIdDT;
import ru.kazachkov.florist.api.model.request.AddUpdateOrder2SellerAuth;
import ru.kazachkov.florist.api.model.request.AddUpdateOrderPhotoAuth;
import ru.kazachkov.florist.api.model.request.AddUpdateOrderSalesAuth;
import ru.kazachkov.florist.api.model.request.EffectFastActionActivity;
import ru.kazachkov.florist.api.model.request.GetCompaniesListAuth;
import ru.kazachkov.florist.api.model.request.GetOrderSellerListAuth;
import ru.kazachkov.florist.api.model.request.GetUsersAtWork;
import ru.kazachkov.florist.api.model.request.UpdateGoodsFastAuthJS;
import ru.kazachkov.florist.api.model.response.AddUpdateOrder2SellerResponse;
import ru.kazachkov.florist.api.model.response.AddUpdateOrderResponse;
import ru.kazachkov.florist.api.model.response.AddUpdateOrderSalesAuthResponse;
import ru.kazachkov.florist.api.model.response.CompaniesList;
import ru.kazachkov.florist.api.model.response.OrderSellerList;
import ru.kazachkov.florist.data.DataSource;
import ru.kazachkov.florist.interfaces.OrderInfo;
import ru.kazachkov.florist.interfaces.OrdersRequestData;
import ru.kazachkov.florist.model.GoodsModel;
import rx.Observable;

/**
 * Created by ishmukhametov on 20.10.16.
 */

public class RemoteDataSource implements DataSource.Remote {

    @Getter
    private final FloristApi floristApi;

    public RemoteDataSource(FloristApi floristApi) {
        this.floristApi = floristApi;
    }

    @Override
    public Observable<List<Category>> getGroupsObs() {
        return null;
    }

    @Override
    public Observable<List<CompanyAtGlanceDT>> getCompaniesList(GetCompaniesListAuth getCompaniesListAuth, Boolean fromRemote) {
        return floristApi.getCompaniesListAuth(getCompaniesListAuth)
                .map(CompaniesList::getOrdersReportData);
    }

    @Override
    public Observable<List<User1CIdDT>> getUsersList(GetUsersAtWork item) {
        return floristApi.getUsersAtWork(item)
                .map(usersList -> usersList.getBody().getGetOrderListAuthResponse().getGetOrderListAuthResult().getStaffData());
    }

    @Override
    public Observable<List<EdIzm>> getUnits() {
        return null;
    }

    @Override
    public Observable<OrderInfo> getOrders(OrdersRequestData orderRequestData) {
        if (!(orderRequestData instanceof GetOrderSellerListAuth))
            return Observable.error(new IllegalArgumentException("OrdersRequestData instance of GetOrderSellerListAuth???"));
        return floristApi.getOrderSellerListAuth((GetOrderSellerListAuth) orderRequestData)
                .map(OrderSellerList::getOrderInfo);
    }

    @Override
    public Observable<AddUpdateOrderResponse> addUpdateOrderPhotoAuth(AddUpdateOrderPhotoAuth addUpdateOrderPhotoAuth) {
        return floristApi.addUpdateOrderPhotoAuth(addUpdateOrderPhotoAuth);
    }

    @Override
    public Observable<List<GoodsModel>> getGoodsModelsList(String storageId) {
        return Observable.error(new NullPointerException("RemoteDataSource not implement method getGoodsModelsList"));
    }

    @Override
    public Observable<OrderFastActions> getOrderFastActions(int type) {
        return Observable.error(new NullPointerException("RemoteDataSource not implement method getGoodsModelsList"));
    }

    @Override
    public Observable<List<Category>> getCategories() {
        return Observable.error(new NullPointerException("RemoteDataSource not implement method getGoodsModelsList"));
    }

    @Override
    public Observable<List<Photo>> getOrderPhotos(String orderId) {
        return Observable.error(new NullPointerException("RemoteDataSource not implement method getGoodsModelsList"));
    }

    @Override
    public Observable<AddUpdateOrder2SellerResponse> addUpdateOrder2PhotoAuth(String sessionId, String storageId, Order2Seller order2Seller) {
        return floristApi.addUpdateOrder2SellerAuth(AddUpdateOrder2SellerAuth.create(sessionId, storageId, order2Seller));
    }

    @Override
    public Observable<AddUpdateOrderSalesAuthResponse> addUpdateOrderSalesAuth(String sessionId, String storageId, OrderData2Save orderData2Save) {
        return floristApi.addUpdateOrderSalesAuth(AddUpdateOrderSalesAuth.create(sessionId, storageId, orderData2Save));
    }

    @Override
    public Observable<GoodsModel> getGoodsModel(String flowerId, String storageId) {
        return Observable.error(new NullPointerException("RemoteDataSource not implement method getGoodsModelsList"));
    }

    @Override
    public Observable<FastUpdate> saveFastUpdate(FastUpdate fastUpdate) {
        return Observable.error(new NullPointerException("RemoteDataSource not implement method getGoodsModelsList"));
    }

    @Override
    public Observable<Object> saveOrderPhotos(List<String> photosUri, String orderId) {
        return Observable.error(new NullPointerException("RemoteDataSource not implement method getGoodsModelsList"));
    }

    @Override
    public Observable<FastUpdate.FastUpdateAuth> updateGoodsFast(UpdateGoodsFastAuthJS updateGoodsFastAuthJS) {
        return floristApi.updateGoodsFastAuthJS(updateGoodsFastAuthJS);
    }

    @Override
    public Observable<OrderDetailedData4> getOrderDetailedData(EffectFastActionActivity fastActionActivity) {
        return floristApi.effectFastActionActivity(fastActionActivity)
                .flatMap(effectFastActionActivityResponse -> {
                    AuthResult authResult = effectFastActionActivityResponse.getAuthResult();
                    if (!authResult.isSuccessful()) {
                        return Observable.error(new Throwable(authResult.getMsgValue()));
                    } else {
                        return Observable.just(effectFastActionActivityResponse.getOrderDetailedData());
                    }
                });
    }
}
