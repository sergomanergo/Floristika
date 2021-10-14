package ru.kazachkov.florist.model.impl;


import android.app.Activity;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableDouble;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ru.kazachkov.florist.BR;
import ru.kazachkov.florist.NewOrderActivity;
import ru.kazachkov.florist.R;
import ru.kazachkov.florist.adapters.BaseAdapter;
import ru.kazachkov.florist.adapters.UpdSpinnerAdapter;
import ru.kazachkov.florist.api.model.CompanyAtGlanceDT;
import ru.kazachkov.florist.api.model.OrderDetailed;
import ru.kazachkov.florist.api.model.Photo;
import ru.kazachkov.florist.api.model.User1CIdDT;
import ru.kazachkov.florist.api.model.UserAuthRole;
import ru.kazachkov.florist.api.model.request.AddNewCompanyAuthJS;
import ru.kazachkov.florist.api.model.request.AddUpdateOrderPhotoAuth;
import ru.kazachkov.florist.api.model.response.CompanyAuthJS;
import ru.kazachkov.florist.data.DataController;
import ru.kazachkov.florist.logic.Error;
import ru.kazachkov.florist.logic.TypeController;
import ru.kazachkov.florist.logic.Typer;
import ru.kazachkov.florist.model.md.NewOrderScreenModel;
import ru.kazachkov.florist.tools.Const;
import ru.kazachkov.florist.tools.PhotoHandler;
import ru.kazachkov.florist.tools.Utils;
import ru.kazachkov.florist.viewmodel.impl.AddVMImpl;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class NewOrderScreenModelImpl implements NewOrderScreenModel, AddVMImpl.OnAddClick {
    private final DataController dataController;
    private NewOrderActivity context;
    private String sessionId;
    private UserAuthRole.SalePnt salePnt;


    private ObservableArrayList<CompanyAtGlanceDT> sellerList = new ObservableArrayList<>();
    private ObservableArrayList<CompanyAtGlanceDT> bayerList = new ObservableArrayList<>();
    private ObservableArrayList<User1CIdDT> usersList = new ObservableArrayList<>();

    private ObservableArrayList<String> photos = new ObservableArrayList<>();

    @Getter
    private ObservableBoolean isSale = new ObservableBoolean(true);
    @Getter
    private ObservableBoolean isShowcase = new ObservableBoolean(false);
    @Getter
    private ObservableBoolean isPrepayment = new ObservableBoolean(false);
    @Getter
    private ObservableBoolean isDelete = new ObservableBoolean(false);
    @Getter
    private ObservableBoolean isComeback = new ObservableBoolean(false);

    @Getter
    private ObservableDouble paidValue = new ObservableDouble();
    @Getter
    private ObservableDouble comebackValue = new ObservableDouble(0);

    @Getter
    private ObservableDouble wasPaid = new ObservableDouble();
    @Getter
    private ObservableDouble wasBankPaid = new ObservableDouble();
    @Getter
    private ObservableBoolean fromCard = new ObservableBoolean(false);

    @Getter
    private ObservableInt sellerPosition = new ObservableInt();
    @Getter
    private ObservableInt bayerPosition = new ObservableInt();
    @Getter
    private ObservableInt authorPosition = new ObservableInt();


    @Getter
    private ObservableBoolean dataHasError = new ObservableBoolean(false);
    private ObservableField<Integer> orderType = new ObservableField<>();
    private ObservableInt prepaymentProgress = new ObservableInt();
    private OrderDetailed orderDetailed = new OrderDetailed();

    @Getter
    private ObservableBoolean moveOrder = new ObservableBoolean();
    private TypeController typeController;

    @Getter
    private boolean enabledChangeType = true;

    @Getter
    private ObservableField<String> sumFinalError = new ObservableField<>();

    private ObservableInt orderDocType;
    private ObservableDouble orderDiscountValue;

    @Getter
    @Setter
    private boolean savedOrder;
    @Getter
    @Setter
    private PhotoHandler photoHandler;
    @Getter
    @Setter
    private String orderId;
    private String sellerNewId;
    private String bayerNewId;
    private String authorNewId;


    public NewOrderScreenModelImpl(NewOrderActivity context,
                                   DataController dataController,
                                   String sessionId,
                                   UserAuthRole.SalePnt salePnt,
                                   boolean savedOrder) {
        this.context = context;
        this.sessionId = sessionId;
        this.salePnt = salePnt;
        this.dataController = dataController;
        this.savedOrder = savedOrder;
        init();
    }

    private void init() {
        Typer typer = new Typer(savedOrder);
        typeController = new TypeController(typer,
                orderType,
                isSale,
                isShowcase,
                isPrepayment,
                isDelete,
                isComeback,
                paidValue,
                comebackValue,
                fromCard);
        photoHandler = new PhotoHandler(photos);
    }


    @Override
    public void bind(OrderDetailed orderDetailed) {
        this.orderDetailed = orderDetailed;
        if (savedOrder) {
            if (orderDetailed.getPaidMoney() > 0 || orderDetailed.getPaidMoneyBank() > 0) {
                enabledChangeType = false;
            }
            if (orderDetailed.getPaidMoney() != 0) {
                wasPaid.set(orderDetailed.getPaidMoney());
                typeController.setStartValue(orderDetailed.getPaidMoney());
                fromCard.set(false);
            } else {
                wasPaid.set(orderDetailed.getPaidMoneyBank());
                typeController.setStartValue(orderDetailed.getPaidMoneyBank());
                fromCard.set(true);
            }
            typeController.setInitBankPaid(orderDetailed.getPaidMoneyBank());
            typeController.setInitPaid(orderDetailed.getPaidMoney());
            orderId = orderDetailed.getOrder1CId();

            sellerNewId = orderDetailed.getBuyer1CId();
            bayerNewId = orderDetailed.getBuyer1CId();
            authorNewId = orderDetailed.getUser1CId();
        } else {
            typeController.setStartValue(orderDetailed.getSumFinal());
        }
        orderDocType = new ObservableInt(orderDetailed.getDocType());
        orderDiscountValue = new ObservableDouble(orderDetailed.getOrderDiscountValue());
    }

    @Override
    public void setOrderDocType(int type) {
        orderDocType.set(type);
        orderDetailed.setDocType(type);
    }

    @Override
    public void setBankValue(int i) {
        typeController.setBankValue(i == 1);
        if (i == 1) {
            wasPaid.set(typeController.getInitBankPaid());
        } else {
            wasPaid.set(typeController.getInitPaid());
        }
    }

    @Override
    public void setPaidValue(double value) {
        typeController.setPaidValue(value);
    }

    @Override
    public void setComebackValue(double value) {
        typeController.setComebackValue(value);
    }


    @Override
    public void setSeller(int position, long l) {
        sellerNewId = sellerList.get(position).getCompany1CId();
    }

    @Override
    public void onAddClick() {

    }

    @Override
    public double getBasePrice() {
        if (orderDetailed != null) return orderDetailed.getSumFinal();
        else return 0;
    }

    @Override
    public ObservableField<Integer> getType() {
        return orderType;
    }

    @Override
    public ObservableInt getProgress() {
        return prepaymentProgress;
    }

    @Override
    public String getComment() {
        return "";
    }

    @Override
    public void onNextType() {
        typeController.onNextType();
    }

    @Override
    public void setProgress(int progress) {

    }

    @Override
    public void setComment(String comment) {
        orderDetailed.setCommentString(comment);
    }

    @Override
    public void setSumFinal(double sum) {
        handleError(sum, aDouble -> aDouble <= 0, sumFinalError, aDouble -> orderDetailed.setSumFinal(aDouble));
    }


    private <T> void handleError(T value, Func1<T, Boolean> prediction, ObservableField<String> error, Action1<T> res) {
        if (prediction.call(value)) {
            error.set(context.getString(R.string.error_value));
        } else {
            sumFinalError.set("");
            res.call(value);
        }
    }

    @Override
    public void setDiscount(Double aDouble) {
        orderDetailed.setOrderDiscountValue(aDouble);
    }

    @Override
    public void onAddDiscount() {
        double value = Math.min(orderDetailed.getOrderDiscountValue() + 0.5, 100.0);
        orderDetailed.setOrderDiscountValue(value);
        orderDiscountValue.set(value);
    }

    @Override
    public void onDeductDiscount() {
        double value = Math.max(orderDetailed.getOrderDiscountValue() - 0.5, 0.0);
        orderDetailed.setOrderDiscountValue(value);
        orderDiscountValue.set(value);
    }

    @Override
    public Activity getContext() {
        return context;
    }

    @Override
    public void setBayer(int position, long id) {
        bayerNewId = bayerList.get(position).getCompany1CId();
    }

    @Override
    public void setAuthor(int position, long id) {
        authorNewId = usersList.get(position).getUser1CId();
    }

    @Override
    public String getSender() {
        return "Андрей";
    }

    @Override
    public String getPointTitle() {
        return salePnt.getStorageName();
    }

    @Override
    public ObservableDouble getDiscount() {
        return orderDiscountValue;
    }

    @Override
    public void onCheckoutOrder() {
        if (!formHasError(orderDetailed)) {
            dataHasError.set(false);
            if (typeController.isBankValue()) {
                switch (typeController.getCurentType()) {
                    case Typer.SALE:
                        orderDetailed.setPaidMoneyBank(orderDetailed.getSumFinal());
                        break;
                    case Typer.PREPAYMENT:
                        orderDetailed.setPaidMoneyBank(typeController.getPaidBank());
                        break;
                    case Typer.SHOWCASE:
                    case Typer.DELETE:
                        orderDetailed.setPaidMoneyBank(typeController.getDeleteBankValue());
                        break;
                    case Typer.COMEBACK:
                        orderDetailed.setPaidMoneyBank(typeController.getBankPaidForComeback());
                        break;
                }
            } else {
                switch (typeController.getCurentType()) {
                    case Typer.SALE:
                        orderDetailed.setPaidMoney(orderDetailed.getSumFinal());
                        break;
                    case Typer.PREPAYMENT:
                        orderDetailed.setPaidMoney(typeController.getPaid());
                        break;
                    case Typer.SHOWCASE:
                    case Typer.DELETE:
                        orderDetailed.setPaidMoney(typeController.getDeleteValue());
                        break;
                    case Typer.COMEBACK:
                        orderDetailed.setPaidMoney(typeController.getPaidForComeback());
                        break;
                }
            }
            orderDetailed.setUser1CId(authorNewId);
            orderDetailed.setBuyer1CId(orderDocType.get() == Const.ORDER_TYPE_MOVE ? sellerNewId : bayerNewId);
            context.onCheckoutOrder(AddUpdateOrderPhotoAuth.create(sessionId, salePnt.getStorage1CId(), orderDetailed), photos);
            orderDetailed.setOrderDate(Utils.currentDateUTC());
        } else {
            dataHasError.set(false);
            dataHasError.set(true);
        }
    }

    private boolean formHasError(OrderDetailed orderDetailed) {
        boolean hasError = false;
        if (orderDetailed.getSumFinal() <= 0) {
            sumFinalError.set(context.getString(R.string.error_value));
            hasError = true;
        }
        return hasError;

    }

    @Override
    public UpdSpinnerAdapter getBayerAdapter() {
        return new UpdSpinnerAdapter(context, R.layout.layout_drop_title_black, bayerList);
    }

    @Override
    public UpdSpinnerAdapter getSellerAdapter() {
        return new UpdSpinnerAdapter(context, R.layout.layout_drop_title_black, sellerList);
    }

    @Override
    public UpdSpinnerAdapter getAuthorAdapter() {
        return new UpdSpinnerAdapter(context, R.layout.layout_drop_title_black, usersList);
    }

    @Override
    public BaseAdapter adapter() {
        return new BaseAdapter(R.layout.item_photo, BR.vm, photos);
    }

    @Override
    public RecyclerView.LayoutManager layoutManager() {
        return new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
    }

    @Override
    public void onAddNewBayer(String s, boolean isBayer, boolean isSeller) {
        AddNewCompanyAuthJS newCompanyAuthJS =
                AddNewCompanyAuthJS.create(AddNewCompanyAuthJS.Data.of(sessionId, salePnt.getStorage1CId(), s, isBayer ? 1 : 0, isSeller ? 1 : 0));
        dataController.getFloristApi()
                .addNewCompanyAuthJS(newCompanyAuthJS)
                .map(CompanyAuthJS::getCompanyData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(companyAtGlanceDT -> {
                    if (isSeller) {
                        sellerList.addAll(companyAtGlanceDT);
                    }
                    if (isBayer) {
                        bayerList.addAll(companyAtGlanceDT);
                    }
                }, Error.onError(context));
    }

    @Override
    public void onAddNewSeller(String s, Boolean aBoolean) {

    }

    public void setBayerObservableList(Observable<List<CompanyAtGlanceDT>> bayerObservableList) {
        bayerObservableList
                .subscribe(companyAtGlanceDT -> {
                    bayerList.addAll(companyAtGlanceDT);

                    if (TextUtils.isEmpty(bayerNewId)) {
                        bayerPosition.set(0);
                        if (bayerList != null && bayerList.size() != 0) {
                            bayerNewId = bayerList.get(0).getCompany1CId();
                        }
                    }

                    int index = 0;
                    for (CompanyAtGlanceDT company : companyAtGlanceDT) {
                        if (TextUtils.equals(bayerNewId, company.getCompany1CId())) {
                            bayerPosition.set(index);
                        }
                        index++;
                    }
                }, Error.onError(context));
    }

    public void setSellerObservableList(Observable<List<CompanyAtGlanceDT>> sellerObservableList) {
        sellerObservableList
                .subscribe(companyAtGlanceDT -> {
                    sellerList.addAll(companyAtGlanceDT);

                    if (TextUtils.isEmpty(sellerNewId)) {
                        sellerPosition.set(0);
                        if (sellerList != null && sellerList.size() != 0) {
                            sellerNewId = sellerList.get(0).getCompany1CId();
                        }
                    }

                    int index = 0;
                    for (CompanyAtGlanceDT company : companyAtGlanceDT) {
                        if (TextUtils.equals(sellerNewId, company.getCompany1CId())) {
                            sellerPosition.set(index);
                        }
                        index++;
                    }
                }, Error.onError(context));
    }

    public void setUserObservableList(Observable<List<User1CIdDT>> userObservableList) {
        userObservableList
                .subscribe(userObservableList1 -> {
                    usersList.addAll(userObservableList1);

                    if (TextUtils.isEmpty(authorNewId)) {
                        authorPosition.set(0);
                        if (usersList != null && usersList.size() != 0) {
                            authorNewId = usersList.get(0).getUser1CId();
                        }
                    }

                    int index = 0;
                    for (User1CIdDT user : userObservableList1) {
                        if (TextUtils.equals(authorNewId, user.getUser1CId())) {
                            authorPosition.set(index);
                        }
                        index++;
                    }
                }, Error.onError(context));
    }

    @Override
    public void onPhotoSuccessfulTake() {
        photoHandler.onPhotoSuccessfulTake();
    }

    @Override
    public List<String> getPhotos() {
        return photoHandler.getPhotos();
    }

    @Override
    public Observable<String> getPhotoObs() {
        return photoHandler.getPhotoObs();
    }

    @Override
    public void setNewPhotoPath(String path) {
        photoHandler.setNewPhotoPath(path);
    }

    public void setPhotosObs(List<Photo> photosObs) {
        for (Photo photo : photosObs) {
            photos.add(photo.getPath());
        }
    }
}
