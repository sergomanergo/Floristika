package ru.kazachkov.florist.order.sellerdata;

import android.app.Application;
import android.content.res.Resources;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.text.TextUtils;

import java.math.BigDecimal;

import ru.kazachkov.florist.BR;
import ru.kazachkov.florist.R;
import ru.kazachkov.florist.interfaces.App;

/**
 * Created by ishmukhametov on 19.10.16.
 */

public class Delivery extends BaseObservable {

    public static String lastReceived;

    private boolean card;
    private BigDecimal additionalExpenses;
    private BigDecimal received;
    private BigDecimal payment;
    private BigDecimal delivery;

    private BigDecimal payment2;
    private BigDecimal delivery2;

    private boolean showAdditionalConsumption;
    private boolean returnPay;
    private Resources resources;
    private boolean skipUpdate = false;

    public Delivery(Resources resources) {
        this.resources = resources;
    }

    public boolean isCard() {
        return card;
    }

    public void setCard(boolean card) {
        this.card = card;
    }

    @Bindable
    public String getAdditionalExpenses() {
        return additionalExpenses.toString();
    }

    public void setAdditionalExpenses(String additionalExpenses) {
        this.additionalExpenses = new BigDecimal(additionalExpenses);
    }

    @Bindable
    public String getReceived() {
        return received.toString();
    }

    public void setReceived(String received) {
        if (TextUtils.isEmpty(received)) return;
        try {
            this.received = new BigDecimal(received);
        } catch (Exception ex) {

        }
        updateDelivery();
    }

    @Bindable
    public boolean isShowAdditionalConsumption() {
        return showAdditionalConsumption;
    }


    public void setShowAdditionalConsumption(boolean showAdditionalConsumption) {
        this.showAdditionalConsumption = showAdditionalConsumption;
    }

    @Bindable
    public String getDialogTitle() {
        return resources.getString( returnPay ? R.string.calculation_returnpay : R.string.calculation_delivery );
    }

    @Bindable
    public String getPayment() {
        return payment.setScale(0, BigDecimal.ROUND_HALF_UP).toString();
    }

    public void setPayment(String payment) {
        if (TextUtils.isEmpty(payment)) return;
        try {
            this.payment = new BigDecimal(payment);
        } catch (Exception ex) {

        }
        updateDelivery();
    }

    @Bindable
    public String getPayment2() {
        return payment2.setScale(0, BigDecimal.ROUND_HALF_UP).toString();
    }

    public void setPayment2(BigDecimal payment2) {
        this.payment2 = payment2;
    }


    @Bindable
    public String getDelivery() {
        return delivery.setScale(0, BigDecimal.ROUND_HALF_UP).toString();
    }

    public void setDelivery(BigDecimal delivery) {
        this.delivery = delivery;
        this.delivery2 = delivery;
    }

    @Bindable
    public String getDelivery2() {
        return delivery2.setScale(0, BigDecimal.ROUND_HALF_UP).toString();
    }

    public void setDelivery2(String delivery2) {
        if (TextUtils.isEmpty(delivery2)) return;
        try {
            this.delivery2 = new BigDecimal(delivery2);
        } catch (Exception ex) {

        }
        //updateDelivery();
        updateReceived();
    }


    public Boolean isReturnPay(){
        return returnPay;
    }

    public void setReturnPay(Boolean returnPay) {
        this.returnPay = returnPay;
    }


    private void updateDelivery() {
        if (received == null || payment == null) return;
        if(skipUpdate) return;
        if(returnPay) {
            skipUpdate = true;
            setDelivery(received.add(payment));
        } else
        if(received.floatValue() <= 0)
            setDelivery(new BigDecimal("0"));
                else
            setDelivery(received.subtract(payment));
        notifyPropertyChanged(BR.delivery);
        notifyPropertyChanged(BR.delivery2);
        skipUpdate = false;
    }

    private void updateReceived() {
        if (received == null || payment == null) return;
        if(skipUpdate) return;

        if(returnPay) {
            skipUpdate = true;
            setReceived(delivery2.subtract(payment).toString());
        }
        notifyPropertyChanged(BR.received);
        skipUpdate = false;
    }
}
