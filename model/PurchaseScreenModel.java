package ru.kazachkov.florist.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableDouble;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.kazachkov.florist.MainActivity;
import ru.kazachkov.florist.R;
import ru.kazachkov.florist.api.model.CompanyAtGlanceDT;
import ru.kazachkov.florist.api.model.GoodDT;
import ru.kazachkov.florist.api.model.Order2Seller;
import ru.kazachkov.florist.api.model.UserAuthRole;
import ru.kazachkov.florist.api.model.request.AddNewCompanyAuthJS;
import ru.kazachkov.florist.api.model.request.AddUpdateOrder2SellerAuth;
import ru.kazachkov.florist.api.model.response.AddUpdateOrder2SellerResponse;
import ru.kazachkov.florist.data.DataController;
import ru.kazachkov.florist.interfaces.Subs;
import ru.kazachkov.florist.interfaces.ToRealmConvert;
import ru.kazachkov.florist.logic.Error;
import ru.kazachkov.florist.tools.Utils;
import ru.kazachkov.florist.viewmodel.vm.PurchaseScreen;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;


@RequiredArgsConstructor(staticName = "of")
public class PurchaseScreenModel implements PurchaseScreen.Model, Subs {
    @NonNull
    @Getter
    Context context;
    @NonNull
    DataController dataController;
    @NonNull
    private String sessionId;
    @NonNull
    private String storageId;

    @Getter
    ObservableField<String> comment = new ObservableField<>();
    @Getter
    ObservableDouble inTotal = new ObservableDouble();
    @Getter
    ObservableDouble retailPrice = new ObservableDouble();
    @Getter
    ObservableDouble inAll = new ObservableDouble();
    @Getter
    ObservableDouble additionalExpenses = new ObservableDouble();
    @Getter
    ObservableBoolean bux = new ObservableBoolean();
    @Getter
    ObservableBoolean cash = new ObservableBoolean();
    @Getter
    ObservableInt type = new ObservableInt();
    @Getter
    ObservableArrayList<CompanyAtGlanceDT> providers = new ObservableArrayList<>();
    @Getter
    ObservableArrayList<UserAuthRole.SalePnt> salePnts = new ObservableArrayList<>();

    @Getter
    ObservableInt selectedProviderPosition = new ObservableInt();
    @Getter
    ObservableInt selectedSalePntPosition = new ObservableInt();

    private List<GoodDT> goodsDT;

    @Setter
    private String commentNew;
    @Setter
    private int docTypeNew;
    private double sumFinalNew;
    @Setter
    private String orderDateNew;
    @Setter
    private String providerNewId;
    private double additionalSpendsNew;
    @Setter
    private int additionalSpendsTypeNew;
    private String order1CId = "";
    BehaviorSubject<AddNewCompanyAuthJS> companiesSubject = BehaviorSubject.create();
    private Subscription companySubs;
    private String salePointNewId;

    @Override
    public void setGoodsDT(List<GoodDT> goodsDT) {
        this.goodsDT = goodsDT;
    }

    @Override
    public void setSum(double sum) {
        inTotal.set(sum);
        retailPrice.set(sum);
        setSumFinalNew(sum);
    }

    @Override
    public void setSalePoint(int position) {
        if (salePnts.size() > position) {
            salePointNewId = salePnts.get(position).getStorage1CId();
        }
    }

    @Override
    public void setProvider(int position) {
        if (providers.size() > position) {
            providerNewId = providers.get(position).getCompany1CId();
        }
    }

    @Override
    public void bind(Order2Seller orderDetailed) {
        if (orderDetailed != null) {
            comment.set(orderDetailed.getCommentString());
            inTotal.set(orderDetailed.getSumFinal());
            retailPrice.set(orderDetailed.getSumFinal());
            inAll.set(orderDetailed.getPaidMoney());
            additionalExpenses.set(orderDetailed.getPaidMoney() - orderDetailed.getSumFinal());

            goodsDT = orderDetailed.getGoods();
            commentNew = orderDetailed.getCommentString();
            docTypeNew = orderDetailed.getDocType();
            sumFinalNew = orderDetailed.getSumFinal();
            orderDateNew = orderDetailed.getOrderDate();
            salePointNewId = orderDetailed.getStorage1CId();
            providerNewId = orderDetailed.getSeller1CId();
            additionalSpendsNew = orderDetailed.getAdditionalSpends();
            additionalSpendsTypeNew = orderDetailed.getAdditionalSpendsType();
            order1CId = orderDetailed.getOrder1CId();
        }
    }

    @Override
    public void setSalePntObservable(Observable<List<UserAuthRole.SalePnt>> salePntObservable) {
        salePntObservable
                .flatMap(Observable::from)
                .subscribe(salePnt -> {
                    salePnts.add(salePnt);
                    if (TextUtils.isEmpty(salePointNewId)) {
                        selectedSalePntPosition.set(0);
                        salePointNewId = salePnt.getStorage1CId();
                    } else if (TextUtils.equals(providerNewId, salePnt.getStorage1CId())) {
                        selectedSalePntPosition.set(providers.size() - 1);
                    }
                }, Error.onError(context));
    }

    @Override
    public void setProvidersObservable(Observable<List<CompanyAtGlanceDT>> bayerObservableList) {
        companySubs = bayerObservableList
                .flatMap(Observable::from).concatWith(companyObs())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(companyAtGlanceDT -> {
                    providers.add(companyAtGlanceDT);
                    if (TextUtils.isEmpty(providerNewId)) {
                        selectedProviderPosition.set(0);
                        providerNewId = companyAtGlanceDT.getCompany1CId();
                    } else if (TextUtils.equals(providerNewId, companyAtGlanceDT.getCompany1CId())) {
                        selectedProviderPosition.set(providers.size() - 1);
                    }
                }, Error.onError(context));
    }

    public Observable<CompanyAtGlanceDT> companyObs() {
        return companiesSubject
                .switchMap(getCompaniesListAuth -> dataController.getFloristApi().addNewCompanyAuthJS(getCompaniesListAuth).subscribeOn(Schedulers.io()))
                .flatMap(companyAuthJS -> Observable.from(companyAuthJS.getCompanyData()));
    }

    @Override
    public void onSend() {
        Order2Seller order2Seller = createOrder();
        if (!hasError(order2Seller)) {
            ProgressDialog progressDialog = ProgressDialog.show(context, context.getString(R.string.handle_requst), context.getString(R.string.order_processing));
            progressDialog.setCancelable(false);
            dataController.getFloristApi()
                    .addUpdateOrder2SellerAuth(AddUpdateOrder2SellerAuth.create(sessionId, salePointNewId, order2Seller))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(addUpdateOrderResponse -> {
                        AddUpdateOrder2SellerResponse.Content content;
                        List<ToRealmConvert> list = new ArrayList<>();
                        try {
                            content = addUpdateOrderResponse.getBody().getResponse().getContent();
                        } catch (NullPointerException e) {
                            return list;
                        }
                        if (content != null && content.getPriceItems() != null)
                            list.addAll(content.getPriceItems());
                        if (content != null && content.getStockItems() != null)
                            list.addAll(content.getStockItems());
                        return list;
                    })
                    .switchMap(toRealmConverts -> dataController.saveToRealmObs(toRealmConverts))
                    .doOnNext(notification -> progressDialog.hide())
                    .subscribe(responseBody -> {
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                        Utils.toast(context, R.string.purchase_saved);
                    }, Error.onError(context));
        }
    }

    @Override
    public void addNewCompany(String name, boolean bayer) {
        AddNewCompanyAuthJS.Data data = AddNewCompanyAuthJS.Data.of(sessionId, storageId, name, bayer ? 1 : 0, 1);
        AddNewCompanyAuthJS addNewCompanyAuthJS = AddNewCompanyAuthJS.create(data);
        companiesSubject.onNext(addNewCompanyAuthJS);
    }

    @Override
    public boolean hasError(Order2Seller order2Seller) {
        return false;
    }

    @Override
    public Order2Seller createOrder() {
        Order2Seller order2Seller = new Order2Seller();
        order2Seller.setOrder1CId(order1CId);
        order2Seller.setCommentString(commentNew);
        order2Seller.setDocType(docTypeNew);
        order2Seller.setGoods(goodsDT);
        order2Seller.setSumFinal(sumFinalNew);
        order2Seller.setOrderDate(orderDateNew);
        order2Seller.setStorage1CId(salePointNewId);
        order2Seller.setAdditionalSpends(additionalSpendsNew);
        order2Seller.setSeller1CId(providerNewId);
        order2Seller.setAdditionalSpendsType(additionalSpendsTypeNew);
        order2Seller.setPaidMoney(sumFinalNew + additionalSpendsNew);
        return order2Seller;
    }

    @Override
    public void setSumFinalNew(double sumFinalNew) {
        this.sumFinalNew = sumFinalNew;
        inAll.set(sumFinalNew + additionalSpendsNew);
    }

    @Override
    public void setAdditionalSpendsNew(double additionalSpendsNew) {
        this.additionalSpendsNew = additionalSpendsNew;
        inAll.set(sumFinalNew + additionalSpendsNew);
    }

    @Override
    public void onSubscribe() {

    }

    @Override
    public void onUnsubscribe() {
        if (companySubs != null) {
            companySubs.unsubscribe();
        }
    }
}
