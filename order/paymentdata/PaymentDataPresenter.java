package ru.kazachkov.florist.order.paymentdata;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import ru.kazachkov.florist.api.model.GoodDT;
import ru.kazachkov.florist.api.model.GoodSMDiscDT;
import ru.kazachkov.florist.api.model.OrderData2Save;
import ru.kazachkov.florist.api.model.OrderDetailed;
import ru.kazachkov.florist.api.model.request.AddUpdateOrderPhotoAuth;
import ru.kazachkov.florist.app.AppPreferences;
import ru.kazachkov.florist.data.DataSource;
import ru.kazachkov.florist.interfaces.OrderData;
import ru.kazachkov.florist.order.sellerdata.Delivery;
import ru.kazachkov.florist.tools.Const;
import ru.kazachkov.florist.tools.SaleMode;
import ru.kazachkov.florist.tools.Utils;
import ru.kazachkov.florist.viewmodel.impl.ComponentVMImpl;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ishmukhametov on 13.10.16.
 */

public class PaymentDataPresenter implements PaymentDataContract.Presenter {
    public static String currentOrderDate = null;

    private Boolean isPayCash = true;

    private final CompositeSubscription subscriptions;

    private final DataSource dataSource;

    private final PaymentDataContract.View view;

    private final int discountStep = 5;
    private final AppPreferences prefs;

    @Getter
    private BigDecimal discount;

    private String received;
    private String infoTextCalculation;
    private String payment;
    private String delivery;
    private BigDecimal basePrice;
    private BigDecimal price;
    private BigDecimal cash = new BigDecimal(0);
    private BigDecimal cashless = new BigDecimal(0);
    private double paidSumCashTotal;
    private double paidSumBankTotal;
    private List<GoodDT> listGoods;
    private String comment;
    private String order1CId;
    private boolean fullPayment;
    private boolean onlySetFullPayment;
    private boolean skipSetFullPayment;
    private boolean finished;
    private boolean finishedVisible;
    private SaleMode docType;
    private Integer isPayTypeCash;
    public String origComment;
    public String realComment;
    private int orderStatus;

    public PaymentDataPresenter(DataSource dataSource, AppPreferences prefs, PaymentDataContract.View view, SaleMode docType, Integer isPayTypeCash) {
        this.dataSource = dataSource;
        this.view = view;
        this.prefs = prefs;
        subscriptions = new CompositeSubscription();
        view.setPresenter(this);
        this.docType = docType;
        this.isPayTypeCash = isPayTypeCash;
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        subscriptions.clear();
    }

    @Override
    public void fullPaymentChanged(boolean isChanged) {

    }

    @Override
    public void calculateDelivery() {
        String recievedValue = ((received!=null&&received!="0")?received:Delivery.lastReceived);
        view.showDeliveryDialog(recievedValue, Double.toString((cash.doubleValue())) );
    }

    @Override
    public void init(String basePrice, String totalPrice, List<GoodDT> listGoods, double discount) {
        this.basePrice = new BigDecimal(basePrice);
        this.price = new BigDecimal(totalPrice);
        this.discount = new BigDecimal(discount);
        this.cash = new BigDecimal(0);
        this.cashless = new BigDecimal(0);
        this.listGoods = listGoods;
        this.comment = "";
        this.order1CId = "";

    }

    @Override
    public void init(String basePrice,String totalPrice, List<GoodDT> listGoods, @Nullable OrderData orderData, double discount) {
        init(basePrice,totalPrice, listGoods, discount);
        if (orderData != null) {
            order1CId = orderData.getOrder1CId();
            comment = orderData.getCommentString();
            origComment = comment;
            if (orderData instanceof OrderDetailed) {
                OrderDetailed order = (OrderDetailed) orderData;
                //paidSumBankToday = order.getTodayPaymentBank();
                paidSumBankTotal = order.getPaidMoneyBank();
                //paidSumCashToday = order.getTodayPayment();
                paidSumCashTotal = order.getPaidMoney();
                orderStatus = order.getPaidStatus();
            }
        }

    }


    public BigDecimal getPaidSumCashTotal() {
        return new BigDecimal(paidSumCashTotal);
    }

    public BigDecimal getPaidSumBankTotal() {
        return new BigDecimal(paidSumBankTotal);
    }

    public double getBalance() {
        return price.doubleValue() - paidSumCashTotal - paidSumBankTotal - cash.doubleValue() - cashless.doubleValue();
    }

    @Override
    public List<String> getDiscountsList() {
        List<String> discounts = new ArrayList<>();
        for (int i = 0; i <= 100; i = i + discountStep) {
//        for (int i = 0; i <= 0; i = i + discountStep) {
            discounts.add(i + " %");
        }
        return discounts;
    }

    @Override
    public void setDiscountPosition(int i) {
        //prevDiscount = discount;
        discount = new BigDecimal(i * discountStep);
        updatePriceWithDiscount();
    }

    @Override
    public int getDiscountPosition() {
        for (double i = 0; i <= 100; i = i + discountStep) {
            if (i == discount.doubleValue()) {
                return (int) (i / discountStep);
            }
        }
        return 0;
    }

    private void updatePriceWithDiscount() {
        /*double oldPrice = price.doubleValue();
        price = basePrice.subtract(basePrice.multiply(discount).divide(new BigDecimal(100), BigDecimal.ROUND_HALF_DOWN));
        for (GoodDT item : listGoods) {
//            item.getPriceAsIs() * item.getSoldItems() ;
            item.setPriceAsIs(item.getPriceAsIs() / oldPrice * price.doubleValue());
            System.out.println();
        }*/
    }

    @Override
    public boolean isFullPayment() {
        return fullPayment;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @Override
    public boolean isFinishedVisible() {
        return finishedVisible;
    }

    @Override
    public void setFinishedVisible(boolean visible) {
        this.finishedVisible = visible;
    }

    @Override
    public void setFullPayment(boolean fullPayment) {
        this.fullPayment = fullPayment;

        this.setFinishedVisible(fullPayment);

        if(this.docType.getDocType() == 150) { //Витрина
            setFinished(false);
        }
        if(this.docType.getDocType() == 160) { //Предоплата
            if(orderStatus >= 30 && orderStatus <= 50) {
                setFinished(false);
                this.fullPayment = true;
            }
        }
        if(this.docType.getDocType() == 170) { //Продажа
            setFinished(true);
            this.fullPayment = true;
        }
        else
        if(!fullPayment) {
            setFinished(false);
        }

        if(this.onlySetFullPayment) {
            this.onlySetFullPayment = false;
            return;
        }

        skipSetFullPayment = true;
        Double balance = getBalance();
        if (fullPayment && balance > 0) {
            if (isPayTypeCash == 1) {
                setCash(String.valueOf( cash.floatValue() + balance ));
            } else if (isPayTypeCash == 2) {
                setCashless(String.valueOf( cashless.floatValue() + balance ));
            } else {
                if (isPayCash) {
                    setCash(String.valueOf( cash.floatValue() + balance ));
                } else {
                    setCashless(String.valueOf( cashless.floatValue() + balance ));
                }
            }
        } else
        {
            if(!fullPayment) {

                if (isPayTypeCash == 1) {
                    setCash("0");
                } else if (isPayTypeCash == 2) {
                    setCashless("0");
                } else {
                    if (isPayCash) {
                        setCash("0");
                    } else {
                        setCashless("0");
                    }
                }

                /*cash = BigDecimal.ZERO;
                cashless = BigDecimal.ZERO;

                this.delivery = "0";
                this.received = "0";
                this.payment = cash.toString();
                view.showDeliveryResult(!this.delivery.equals("0"), true);*/
            }
        }
    }


    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public void deliveryResult(String received, String payment, String delivery) {

        boolean returnPay = (getCash().floatValue() < 0);

        Delivery.lastReceived = received;

        this.received = received;
        this.payment = payment;
        this.delivery = delivery;

        if(returnPay) {
            setCash("-" + payment);
        } else
            setCash(payment);

        if(this.delivery.equals(this.received)) {
            this.received = "0";
            this.delivery = "0";
        }
        if(this.getDelivery() != null)
        view.showDeliveryResult(!this.delivery.equals("0"), !this.delivery.equals("0"));
    }

    @Override
    public String getReceived() {
        return received;
    }

    @Override
    public String getInfoTextCalculation() {
       /* if (this.payment != null && !this.payment.equals("0") || this.cashless != null && !this.cashless.toString().equals("0")) {

            StringBuilder builder = new StringBuilder();
            int counter = 1;

            if (!this.received.equals("0")) {
                builder.append(counter + ". Получить наличными " + this.received + " р.");
                counter++;
            }
            if (!this.cashless.toString().equals("0")) {
                if (counter != 1) {
                    builder.append("\n");
                }
                builder.append(counter + ". Провести через терминал " + this.cashless + " р.");
                counter++;
            }
            if (!this.delivery.equals("0")) {
                if (counter != 1) {
                    builder.append("\n");
                }
                builder.append(counter + ". Выдать сдачу " + this.delivery + " р.");
//                counter++;
            }
            this.infoTextCalculation = builder.toString();
        } else {
            this.infoTextCalculation = "";
        }*/

        this.infoTextCalculation = "";
        return this.infoTextCalculation;
    }

    @Override
    public void setInfoTextCalculation(String infoTextCalculation) {
        this.infoTextCalculation = infoTextCalculation;
    }

    @Override
    public String getPayment() {
        return payment;
    }

    @Override
    public String getDelivery() {
        return delivery;
    }

    @Override
    public BigDecimal getBasePrice() {
        return basePrice;
    }

    @Override
    public void setBasePrice(String basePrice) {
        try {
            this.basePrice = new BigDecimal(new BigDecimal(basePrice).divide(new BigDecimal(100).subtract(discount).divide(new BigDecimal(100))).floatValue());
            updatePriceWithDiscount();
           /* if (price.compareTo(cash) > 0) {
                this.cash = new BigDecimal(price.toString());
            } else {
                this.cash = new BigDecimal(0);
            }*/
        } catch (NumberFormatException ignored) {

        }
        if (isPayTypeCash == 1 || isPayTypeCash == 2) {
            setFullPayment(true);
        }
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal newPrice){
        double multiplier = newPrice.doubleValue() / price.doubleValue();
        for (GoodDT goodDT : listGoods) {
            goodDT.setPriceBought( goodDT.getPriceBought() * multiplier );
        }
        price = newPrice;
    }

    private void updateCashless() {
        if (fullPayment) {
            this.cashless = new BigDecimal(getBalance()).subtract(cash);
        }
    }

    private void updateCash() {
        if (fullPayment) {
            this.cash = new BigDecimal(getBalance()).subtract(cashless);
        }
    }

    @Override
    public BigDecimal getCash() {
        return cash;
    }

    @Override
    public void setCash(String cash) {
        /*if(cash.contains("-")) { //для возврата

            BigDecimal cashValue = new BigDecimal(cash);
            if (cashValue.equals(this.cash)) return;
            this.cash = cashValue;

            return;
        }*/

        if (TextUtils.isEmpty(cash) /*|| cash.contains("-")*/) return;

        BigDecimal cashValue = new BigDecimal(cash);
        if (cashValue.equals(this.cash)) return;

        if (TextUtils.isEmpty(cash)) {
            cashValue = new BigDecimal(0);
        }
        this.cash = new BigDecimal(0); //clear before get Balance
        double diff = 0;
        double cl = 0;

        Double balance = getBalance();
        if(cashValue.floatValue() > balance) {
            diff = cashValue.floatValue() - balance;
            BigDecimal oldValue = cashValue;

            cl = cashless.floatValue() - diff;
            if(cl < 0) cl = 0;

            this.delivery = new BigDecimal(cashValue.floatValue() - balance - (cashless.floatValue() - cl) ).setScale(0, BigDecimal.ROUND_HALF_UP).toString();
            this.received = cashValue.setScale(0, BigDecimal.ROUND_HALF_UP).toString();
            cashValue = new BigDecimal(balance  + (cashless.floatValue() - cl)) ;
            this.payment = cashValue.setScale(0, BigDecimal.ROUND_HALF_UP).toString();
            view.showDeliveryResult(!this.delivery.equals("0"), true);
        } else {

            if(this.fullPayment && (this.cashless.floatValue() >= 0) && (cashValue.floatValue() < balance)) {
                this.cash = cashValue;
                setCashless(String.valueOf(this.cashless.floatValue() + (balance - cashValue.floatValue())));

                if(cashValue.floatValue() <= balance) {

                    if(this.received != "0" && this.received != null) {
                        this.delivery = new BigDecimal(Double.parseDouble(this.received) - cashValue.floatValue()).setScale(0, RoundingMode.HALF_UP).toString();;

                        if(this.delivery == "0" || this.delivery.equals(this.received)) {
                            this.delivery = "0";
                            this.received = "0";
                        }
                    }
                    else {
                        this.delivery = "0"; //Чтобы небыло null
                        this.received = "0";
                    }
                }
                return;
            } else
            if(balance > 0) {
                this.fullPayment = false;
                this.onlySetFullPayment = false; //skip calc on setFullPayment
            }

            this.payment = cashValue.setScale(0, BigDecimal.ROUND_HALF_UP).toString();

            if(this.received != "0" && this.received != null) {
                this.delivery = new BigDecimal(Double.parseDouble(this.received) - cashValue.floatValue()).setScale(0, RoundingMode.HALF_UP).toString();
                if(this.delivery == "0" || this.delivery.equals(this.received)) {
                    this.delivery = "0";
                    this.received = "0";
                }
            } else {
                this.delivery = "0"; //Чтобы небыло null
                this.received = "0";
            }

            view.showDeliveryResult(!this.delivery.equals("0"), true);
        }

        if(balance - cashValue.doubleValue() == 0) {

            this.fullPayment = true;
            if(!skipSetFullPayment)
            this.onlySetFullPayment = true; //skip calc on setFullPayment
            this.skipSetFullPayment = false;
        }

        this.cash = cashValue;

        if(diff > 0) {
            setCashless(String.valueOf(cl));
        }

        //updateCashless();
    }

    @Override
    public BigDecimal getCashless() {
        return cashless;
    }

    @Override
    public void setCashless(String cashless) {

        /*if(cashless.contains("-")) { //для возврата

            BigDecimal cashlessValue = new BigDecimal(cashless);
            if (cashlessValue.equals(this.cashless)) return;
            this.cashless = cashlessValue;

            return;
        }*/
        if (TextUtils.isEmpty(cashless) /*|| cashless.contains("-")*/) return;

        BigDecimal cashlessValue = new BigDecimal(cashless);
        if (cashlessValue.equals(this.cashless)) return;

        if (TextUtils.isEmpty(cashless)) {
            cashlessValue = new BigDecimal(0);
        }

        this.cashless = new BigDecimal(0); //clear before get Balance
        double diff = 0;
        double cl = 0;

        Double balance = getBalance();

        BigDecimal startCashlessValue = cashlessValue;
        if(cashlessValue.floatValue() >= balance) {
            diff = cashlessValue.floatValue() - balance;

            cl = cash.floatValue() - diff;
            if(cl < 0) cl = 0;
            if(cash.floatValue() < 0) cl -= Math.abs(cash.floatValue());
            cashlessValue = new BigDecimal(balance + (cash.floatValue() - cl));
        } else {

            if(this.fullPayment && (this.cash.floatValue() >= 0) && (cashlessValue.floatValue() < balance)) {
                this.cashless = cashlessValue;
                setCash(String.valueOf(this.cash.floatValue() + (balance - cashlessValue.floatValue())));
                return;
            } else
            if(balance > 0) {
                this.fullPayment = false;
                this.onlySetFullPayment = false; //skip calc on setFullPayment
            }
        }
        if(balance - cashlessValue.doubleValue() == 0 ) { //оплачено полностью

            this.fullPayment = true;
            if(!skipSetFullPayment)
            this.onlySetFullPayment = true; //skip calc on setFullPayment
            this.skipSetFullPayment = false;
        }

        this.cashless = cashlessValue;

        if(diff > 0) {
            setCash(String.valueOf(cl));
        }




        //updateCash();
    }


    @Override
    public void prepareAndSendOrder(String user1CId, String client1cId, List<String> photosUri, SaleMode saleMode) {

        if (TextUtils.isEmpty(user1CId)) {
            throw new NullPointerException("user1CId is can't be empty");
        }

        if (price.equals(new BigDecimal(0))) {
            view.showMessage("Заказ не может быть оформлен с нулевой ценой");
            return;
        }

        prepareAndSendPurchase(user1CId, client1cId, photosUri);
//        if (saleMode == SaleMode.PURCHASE) {
//        } else {
//            prepareAndSendOrder(user1CId, client1cId, photosUri, saleMode == SaleMode.PREPAY);
//        }


    }

    private void prepareAndSendOrder(String user1CId, String client1cId, List<String> photosUri, boolean isPrePay) {
        if (isPrePay) {
            GoodDT goodDT = listGoods.get(0);
            goodDT.setPriceAsIs(this.getBasePrice().doubleValue());
            goodDT.setPriceBought(this.getBasePrice().doubleValue());
        }
        OrderDetailed orderDetailed = OrderDetailed.builder()
                .docType(Const.ORDER_TYPE_SALE)
                .user1CId(user1CId)
                .commentString(comment)
                .orderDiscountValue(discount.doubleValue())
                .goods(listGoods)
                .sumFinal(price.doubleValue())
                .paidMoney(cash.doubleValue())
                .paidMoneyBank(cashless.doubleValue())
                .order1CId(order1CId)
                .orderDate(Utils.currentDateUTC())
                .storage1CId(prefs.getSalePntId())
                .buyer1CId(client1cId)
                .build();

        AddUpdateOrderPhotoAuth addUpdateOrderPhotoAuth = AddUpdateOrderPhotoAuth.create(prefs.getSessionId(), prefs.getSalePntId(), orderDetailed);
        view.showProgress();


        Subscription subscription = dataSource.addUpdateOrderPhotoAuth(addUpdateOrderPhotoAuth)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .switchMap(addUpdateOrderResponse -> {
                    String orderId;
                    try {
                        orderId = addUpdateOrderResponse.getBody().getResponse().getContent().getOrderShortData().getOrder1CId();
                    } catch (NullPointerException e) {
                        return Observable.empty();
                    }
                    return dataSource.saveOrderPhotos(photosUri, orderId);
                })
                .doOnNext(addUpdateOrderResponse -> view.hideProgress())
                .subscribe(addUpdateOrderResponse -> {
                    view.showScreenAfterSendOrder();
                }, view::showError);

        subscriptions.add(subscription);
    }

    private void prepareAndSendPurchase(String user1CId, String client1cId, List<String> photosUri) {
        view.showProgress();
        setComment(realComment);
        Subscription subscription = Observable.from(listGoods)
                .map(goodDT -> new GoodSMDiscDT(goodDT, new BigDecimal(goodDT.getPriceBought())))
                .toList()
                .flatMap(goodSMDiscDTs -> Observable.from(goodSMDiscDTs)
                        .map(goodSMDiscDT -> new BigDecimal(goodSMDiscDT.getDiscVal()))
                        .reduce(BigDecimal::add)
                        .last()
                        .map(bigDecimal -> getRequestModel(client1cId, goodSMDiscDTs, bigDecimal.toString())))
                //.delay(5, TimeUnit.SECONDS)
                .flatMap(orderData2Save -> dataSource.addUpdateOrderSalesAuth(prefs.getSessionId(), prefs.getSalePntId(), orderData2Save))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .switchMap(addUpdateOrderResponse -> {
                    String orderId;
                    try {
                        orderId = addUpdateOrderResponse.getOrder1CId();
                    } catch (NullPointerException e) {
                        return Observable.just(addUpdateOrderResponse);
                    }
                    return dataSource.saveOrderPhotos(photosUri, orderId);
                })
                .doOnEach(addUpdateOrderResponse -> view.hideProgress())
                .subscribe(addUpdateOrderResponse -> {
                    view.showScreenAfterSendOrder();
                    //}, view::showError);

                }, e -> {
                    e.printStackTrace();
                });

        subscriptions.add(subscription);
    }


    private OrderData2Save getRequestModel(String client1cId, List<GoodSMDiscDT> list, String sumFinal) {
        return OrderData2Save.builder()
                .order1CId(order1CId)
                .orderDate(currentOrderDate)
                .storage1CId(prefs.getSalePntId())
                .docType(docType.getDocType())
                .commentString(comment)
                .sumFinal(price.doubleValue())
                .paidSumCashToday(cash.doubleValue())
                .paidSumBankToday(cashless.doubleValue())
                .responciblePersonName(""/*ответвенный*/)
                .orderDiscount(discount.doubleValue())
                .secondPart1CId(client1cId)
                .saleOrderType(0)
                .goods(list)
                .isPrepaid(docType.equals(SaleMode.PREPAY) ? 1 : 0)
                .isFinished(finished ? 1: 0)
                .isChequed(0)
                .build();
    }

    public Boolean getIsPayCash() {
        return isPayCash;
    }

    public void setIsPayCash(Boolean isPayCash) {
        this.isPayCash = isPayCash;

        if(isPayTypeCash > 0) setFullPayment(true);
    }
}
