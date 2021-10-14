package ru.kazachkov.florist.data;

import java.util.List;

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
import ru.kazachkov.florist.api.model.request.AddUpdateOrderPhotoAuth;
import ru.kazachkov.florist.api.model.request.EffectFastActionActivity;
import ru.kazachkov.florist.api.model.request.GetCompaniesListAuth;
import ru.kazachkov.florist.api.model.request.GetUsersAtWork;
import ru.kazachkov.florist.api.model.request.UpdateGoodsFastAuthJS;
import ru.kazachkov.florist.api.model.response.AddUpdateOrder2SellerResponse;
import ru.kazachkov.florist.api.model.response.AddUpdateOrderResponse;
import ru.kazachkov.florist.api.model.response.AddUpdateOrderSalesAuthResponse;
import ru.kazachkov.florist.interfaces.OrderInfo;
import ru.kazachkov.florist.interfaces.OrdersRequestData;
import ru.kazachkov.florist.model.GoodsModel;
import rx.Observable;

/**
 * Created by ishmukhametov on 20.10.16.
 */

public interface DataSource {

    Observable<List<Category>> getGroupsObs();

    Observable<List<CompanyAtGlanceDT>> getCompaniesList(GetCompaniesListAuth getCompaniesListAuth, Boolean fromRemote);

    Observable<List<User1CIdDT>> getUsersList(GetUsersAtWork item);

    Observable<List<EdIzm>> getUnits();

    Observable<OrderInfo> getOrders(OrdersRequestData orderRequestData);

    Observable<AddUpdateOrderResponse> addUpdateOrderPhotoAuth(AddUpdateOrderPhotoAuth addUpdateOrderPhotoAuth);

    Observable<List<GoodsModel>> getGoodsModelsList(String storageId);

    Observable<OrderFastActions> getOrderFastActions(int type);

    Observable<List<Category>> getCategories();

    Observable<Object> saveOrderPhotos(List<String> photosUri, String orderId);

    Observable<List<Photo>> getOrderPhotos(String orderId);

    Observable<AddUpdateOrder2SellerResponse> addUpdateOrder2PhotoAuth(String sessionId, String storageId, Order2Seller order2Seller);

    Observable<AddUpdateOrderSalesAuthResponse> addUpdateOrderSalesAuth(String sessionId, String storageId, OrderData2Save orderData2Save);

    Observable<GoodsModel> getGoodsModel(String flowerId, String storageId);

    Observable<FastUpdate> saveFastUpdate(FastUpdate fastUpdate);

    Observable<OrderDetailedData4> getOrderDetailedData(EffectFastActionActivity fastActionActivity);

    interface Common extends DataSource {
        Observable<FastUpdate> updateGoodsFast(String sessionId, String storageId);
    }

    interface Local extends DataSource {
        Observable<List<CompanyAtGlanceDT>> saveCompanies(List<CompanyAtGlanceDT> companyAtGlanceDTs1);

        String getRealmFakeItem();
    }

    interface Remote extends DataSource {
        Observable<FastUpdate.FastUpdateAuth> updateGoodsFast(UpdateGoodsFastAuthJS updateGoodsFastAuthJS);
    }

}
