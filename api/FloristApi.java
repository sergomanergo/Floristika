package ru.kazachkov.florist.api;

import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import ru.kazachkov.florist.api.model.FastUpdate;
import ru.kazachkov.florist.api.model.UserAuthRole;
import ru.kazachkov.florist.api.model.request.AddNewCompanyAuthJS;
import ru.kazachkov.florist.api.model.request.AddUpdateCompanyAuthJS;
import ru.kazachkov.florist.api.model.request.AddUpdateGoodItemAuthJS;
import ru.kazachkov.florist.api.model.request.AddUpdateOrder2SellerAuth;
import ru.kazachkov.florist.api.model.request.AddUpdateOrderPhotoAuth;
import ru.kazachkov.florist.api.model.request.AddUpdateOrderSalesAuth;
import ru.kazachkov.florist.api.model.request.DeactivateSessionJS;
import ru.kazachkov.florist.api.model.request.EffectFastActionActivity;
import ru.kazachkov.florist.api.model.request.EffectFastActionActivity4;
import ru.kazachkov.florist.api.model.request.GetCompaniesListAuth;
import ru.kazachkov.florist.api.model.request.GetOrder2SellerDetailedAuth;
import ru.kazachkov.florist.api.model.request.GetOrderListAuth;
import ru.kazachkov.florist.api.model.request.GetOrderSellerListAuth;
import ru.kazachkov.florist.api.model.request.GetUsersAtWork;
import ru.kazachkov.florist.api.model.request.SessionPassword;
import ru.kazachkov.florist.api.model.request.UpdateGoodsFastAuthJS;
import ru.kazachkov.florist.api.model.response.AddUpdateOrder2SellerResponse;
import ru.kazachkov.florist.api.model.response.AddUpdateOrderResponse;
import ru.kazachkov.florist.api.model.response.AddUpdateOrderSalesAuthResponse;
import ru.kazachkov.florist.api.model.response.CompaniesList;
import ru.kazachkov.florist.api.model.response.CompanyAuthJS;
import ru.kazachkov.florist.api.model.response.EffectFastActionActivity4Response;
import ru.kazachkov.florist.api.model.response.EffectFastActionActivityResponse;
import ru.kazachkov.florist.api.model.response.ItemDataJS;
import ru.kazachkov.florist.api.model.response.OrderDetailedJS;
import ru.kazachkov.florist.api.model.response.OrderList;
import ru.kazachkov.florist.api.model.response.OrderSellerDetaildAuth;
import ru.kazachkov.florist.api.model.response.OrderSellerList;
import ru.kazachkov.florist.api.model.response.UsersList;
import rx.Observable;

public interface FloristApi {

    @Headers("Content-Type: text/xml; charset=utf-8")
    @POST("/fs1c.asmx?op=CheckSessionPasswordJS")
    Observable<UserAuthRole> checkSessionPassword(@Body SessionPassword sessionPassword);

    @Headers("Content-Type: text/xml; charset=utf-8")
    @POST("/fs1c.asmx")
    Observable<UserAuthRole> checkSessionPasswordJS4(@Body SessionPassword sessionPassword);

    @Headers("Content-Type: text/xml; charset=utf-8")
    @POST("/fs1c.asmx?op=GetOrderListAuth4")
    Observable<OrderList> getOrderListAuth(@Body GetOrderListAuth orderListAuth);

    @Headers("Content-Type: text/xml; charset=utf-8")
    @POST("/fs1c.asmx?op=GetOrderSellerListAuth")
    Observable<OrderSellerList> getOrderSellerListAuth(@Body GetOrderSellerListAuth orderListAuth);

    @Headers("Content-Type: text/xml; charset=utf-8")
    @POST("/fs1c.asmx?op=GetOrder2SellerDetailedAuth")
    Observable<OrderSellerDetaildAuth> getOrder2SellerDetailedAuth(@Body GetOrder2SellerDetailedAuth info);

    @Headers("Content-Type: application/soap+xml; charset=utf-8")
    @POST("/fs1c.asmx?op=AddUpdateOrderPhotoAuth")
    Observable<AddUpdateOrderResponse> addUpdateOrderPhotoAuth(@Body AddUpdateOrderPhotoAuth addUpdateOrderPhotoAuth);

    @Headers("Content-Type: text/xml; charset=utf-8")
    @POST("/fs1c.asmx?op=GetCompaniesListAuthJS4")
    Observable<CompaniesList> getCompaniesListAuth(@Body GetCompaniesListAuth getCompaniesListAuth);

    @Headers("Content-Type: application/soap+xml; charset=utf-8")
    @POST("/fs1c.asmx?op=AddUpdateCompanyAuthJS")
    Observable<ItemDataJS> AddUpdateCompanyAuthJS(@Body AddUpdateCompanyAuthJS item);

    @Headers("Content-Type: application/soap+xml; charset=utf-8")
    @POST("/fs1c.asmx?op=AddUpdateGoodItemAuthJS")
    Observable<ItemDataJS> addUpdateGoodItemAuth(@Body AddUpdateGoodItemAuthJS item);

    @Headers("Content-Type: application/soap+xml; charset=utf-8")
    @POST("/fs1c.asmx?op=GetUsersAtWork")
    Observable<UsersList> getUsersAtWork(@Body GetUsersAtWork getUsersAtWork);

    @Headers("Content-Type: application/soap+xml; charset=utf-8")
    @POST("/fs1c.asmx?op=DeactivateSessionJS")
    Observable<ResponseBody> deactivateSessionJS(@Body DeactivateSessionJS deactivateSessionJS);

    @Headers("Content-Type: application/soap+xml; charset=utf-8")
    @POST("/fs1c.asmx?op=UpdateGoodsFastAuthJS")
    Observable<FastUpdate.FastUpdateAuth> updateGoodsFastAuthJS(@Body UpdateGoodsFastAuthJS updateGoodsFastAuthJS);

    @Headers("Content-Type: application/soap+xml; charset=utf-8")
    @POST("/fs1c.asmx?op=AddNewCompanyAuthJS")
    Observable<CompanyAuthJS> addNewCompanyAuthJS(@Body AddNewCompanyAuthJS addNewCompanyAuthJS);

    @Headers("Content-Type: application/soap+xml; charset=utf-8")
    @POST("/fs1c.asmx?op=AddUpdateOrder2SellerAuth")
    Observable<AddUpdateOrder2SellerResponse> addUpdateOrder2SellerAuth(@Body AddUpdateOrder2SellerAuth auth);

    @Headers("Content-Type: application/soap+xml; charset=utf-8")
    @POST("/fs1c.asmx?op=EffectFastActionActivity")
    Observable<EffectFastActionActivityResponse> effectFastActionActivity(@Body EffectFastActionActivity effectFastActionActivity);

    @Headers("Content-Type: application/soap+xml; charset=utf-8")
    @POST("/fs1c.asmx?op=EffectFastActionActivity4DateStr")
    Observable<EffectFastActionActivity4Response> effectFastActionActivity4(@Body EffectFastActionActivity4 effectFastActionActivity4);


    @Headers("Content-Type: application/soap+xml; charset=utf-8")
    @POST("/fs1c.asmx?op=AddUpdateOrderSalesAuth")
    Observable<AddUpdateOrderSalesAuthResponse> addUpdateOrderSalesAuth(@Body AddUpdateOrderSalesAuth addUpdateOrderSalesAuth);
}
