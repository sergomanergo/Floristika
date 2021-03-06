package ru.kazachkov.florist.order.paymentdata;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableField;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ru.kazachkov.florist.BR;
import ru.kazachkov.florist.order.paymentdata.PaymentDataContract;
import ru.kazachkov.florist.tools.SaleMode;
import ru.kazachkov.florist.tools.Utils;

/**
 * Created by ishmukhametov on 13.10.16.
 */

public class PaymentDataViewModel extends BaseObservable {

    private final PaymentDataContract.Presenter presenter;
    private boolean showDelivery;
    private boolean hideButtonDelivery;
    private SaleMode saleMode = SaleMode.SALE;
    private OnChangeBasePrice onChange;

    public MutableLiveData<Double> liveBalance = new MutableLiveData<>();
    public MutableLiveData<List<String>> livePayments = new MutableLiveData<>();

    public PaymentDataViewModel(PaymentDataContract.Presenter presenter) {
        this.presenter = presenter;
        livePayments.setValue(paymentHistory.get());
    }

    @Bindable
    public SaleMode getSaleMode() {
        return saleMode;
    }

    public void setSaleMode(SaleMode saleMode) {
        this.saleMode = saleMode;
        notifyPropertyChanged(BR.saleMode);
    }

    @Bindable
    public String getComment() {
        return presenter.getComment();
    }

    public void setComment(String comment) {
        presenter.setComment(comment);
    }

    @Bindable
    public boolean isFullPayment() {
        notifyPropertyChanged(BR.fullPayment);
        return presenter.isFullPayment();
    }

    public void setFullPayment(boolean fullPayment) {
        presenter.setFullPayment(fullPayment);
        notifyPropertyChanged(BR.cash);
        notifyPropertyChanged(BR.cashless);
        notifyPropertyChanged(BR.finished);
    }
    @Bindable
    public boolean isFinished() {
        notifyPropertyChanged(BR.finished);
        return presenter.isFinished();
    }

    public void setFinished(boolean finished) {
        presenter.setFinished(finished);
        notifyPropertyChanged(BR.finished);
    }


    @Bindable
    public boolean isFinishedVisible() {
        notifyPropertyChanged(BR.finishedVisible);
        return presenter.isFinishedVisible();
    }

    public void setFinishedVisible(boolean visible) {
        presenter.setFinishedVisible(visible);
        notifyPropertyChanged(BR.finishedVisible);
    }


    @Bindable
    public String getReceived() {
        return presenter.getReceived();
    }

    @Bindable
    public String getInfoTextCalculation() {
        return presenter.getInfoTextCalculation();
    }

    @Bindable
    public void setInfoTextCalculation(String name) {
        presenter.setInfoTextCalculation(name);
    }

    @Bindable
    public String getPayment() {
        return presenter.getPayment();
    }

    @Bindable
    public String getSurrender() {
        return presenter.getPayment();
    }

    @Bindable
    public String getDelivery() {
        return presenter.getDelivery();
    }

    @Bindable
    public boolean isHideButtonDelivery() {
        return hideButtonDelivery;
    }

    @Bindable
    public boolean getShowDelivery() {
        return showDelivery;
    }

    public void setShowDelivery(boolean showDelivery, boolean hideButtonDelivery) {
        this.showDelivery = showDelivery;
        this.hideButtonDelivery = hideButtonDelivery;
        notifyPropertyChanged(BR.showDelivery);
        notifyPropertyChanged(BR.hideButtonDelivery);
        notifyPropertyChanged(BR.received);
        notifyPropertyChanged(BR.payment);
        notifyPropertyChanged(BR.surrender);
        notifyPropertyChanged(BR.delivery);
        notifyPropertyChanged(BR.infoTextCalculation);
        notifyPropertyChanged(BR.balance);
        notifyPropertyChanged(BR.cash);
        notifyPropertyChanged(BR.cashless);
        notifyPropertyChanged(BR.finished);
        toDoList.notifyChange();
        paymentHistory.notifyChange();
        notifyPropertyChanged(BR.balance);
    }

    public void updatePayment(){
        notifyPropertyChanged(BR.payment);
    }

    @Bindable
    public String getPrice() {
        return formatThousand(presenter.getPrice().setScale(0, RoundingMode.DOWN)) + " p.";
    }

    private String formatThousand(BigDecimal value){
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(value);
    }

    public void setPrice(String price) {
        if (price.length() != 0) {
            presenter.setBasePrice(price);
            if (onChange != null) {
                onChange.onChange(presenter.getBasePrice());
                notifyPropertyChanged(BR.cash);
                notifyPropertyChanged(BR.cashless);
            }
        }
    }

    @Bindable
    public String getCash() {
        return presenter.getCash().setScale(0, BigDecimal.ROUND_HALF_UP).toString();
    }

    public void setCash(String cash) {
        presenter.setCash(cash);
        //notifyPropertyChanged(BR.cashless);
        toDoList.notifyChange();
        //paymentHistory.notifyChange();
        notifyPropertyChanged(BR.balance);
    }

    @Bindable
    public String getCashless() {
        return presenter.getCashless().setScale(0, BigDecimal.ROUND_HALF_UP).toString();
    }

    public void setCashless(String cashless) {
        presenter.setCashless(cashless);
        //notifyPropertyChanged(BR.cash);
        toDoList.notifyChange();
        //paymentHistory.notifyChange();
        notifyPropertyChanged(BR.balance);
    }

    @Bindable
    public String getRetailPrice() {
        return presenter.getBasePrice().toString();
    }

    void notifyPriceChanged() {
        notifyPropertyChanged(BR.price);
    }

    public void onChangeBasePrice(OnChangeBasePrice onChange) {
        this.onChange = onChange;
    }

    @Bindable
    public String getBalance() {
        double balance = presenter.getBalance();
        liveBalance.setValue(balance);
        return (balance == 0.0) ? "" : "??????????????: " + new BigDecimal(balance).toPlainString() + " ??.";
    }

    public void setBalance(String balance) {

    }

    public final ObservableField<List<String>> toDoList = new ObservableField<List<String>>(Arrays.asList()){
        @Override
        public List<String> get() {
            List<String> list = new ArrayList<>();
            int i = 1;

            double received;
            try {
                received = Double.parseDouble(presenter.getReceived());
            }
            catch (Exception e)
            {
                received = 0;
            }

            double delivery;
            try {
                delivery = Double.parseDouble(presenter.getDelivery());
            }
            catch (Exception e)
            {
                delivery = 0;
            }

            if(presenter.getCashless().floatValue() < 0) {
                list.add(String.valueOf(i)+ ". ?????????????? ???????????? " + presenter.getCashless().abs().floatValue() + " ??.");
                i++;
            }

            if(presenter.getCash().floatValue() < 0) {
                if (received > 0) {
                    list.add(String.valueOf(i) + ". ???????????????? ?????????????????? " + presenter.getReceived() + " ??.");
                    i++;
                    list.add(String.valueOf(i)+ ". ?????????????? ?????????????????? " + presenter.getDelivery() + " ??.");
                    i++;
                } else {
                    list.add(String.valueOf(i)+ ". ?????????????? ?????????????????? " + presenter.getCash().abs().floatValue() + " ??.");
                    i++;
                }
            } else {
                if (received > 0) {
                    list.add(String.valueOf(i) + ". ???????????????? ?????????????????? " + presenter.getReceived() + " ??.");
                    i++;
                } else {
                    if (presenter.getCash().compareTo(BigDecimal.ZERO) > 0) {
                        list.add(String.valueOf(i) + ". ???????????????? ?????????????????? " + presenter.getCash().toPlainString() + " ??.");
                        i++;
                    }
                }
                if (delivery > 0) {
                    list.add(String.valueOf(i) + ". ???????????? ?????????? " + presenter.getDelivery() + " ??.");
                    i++;
                }
            }

            if(presenter.getCashless().compareTo(BigDecimal.ZERO) > 0){
                list.add(String.valueOf(i)+ ". ???????????????? ???????????????????????? " + presenter.getCashless().toPlainString() + " ??.");
                i++;
            }
            BigDecimal bal = new BigDecimal(presenter.getBalance());
            if(bal.compareTo(BigDecimal.ZERO) != 0){
                list.add(String.valueOf(i)+ ". ???????????????? ???? ?????????????? " + bal.setScale(0, BigDecimal.ROUND_HALF_UP).toString() + " ??.");
                i++;
            }
            return list;
        }
    };
    public final ObservableField<List<String>> paymentHistory =  new ObservableField<List<String>>(Arrays.asList()){
        @Override
        public List<String> get() {
            List<String> list = new ArrayList<>();
            if(presenter.getPaidSumCashTotal().compareTo(BigDecimal.ZERO) > 0){
                list.add(" ?????????? ?????????????????? " + presenter.getPaidSumCashTotal().toPlainString() + " ??.");
            }
            if(presenter.getPaidSumBankTotal().compareTo(BigDecimal.ZERO) > 0){
                list.add("?????????? ???????????????????????? " + presenter.getPaidSumBankTotal().toPlainString() + " ??.");
            }
            return list;
        }
    };


    public interface OnChangeBasePrice {
        void onChange(BigDecimal value);
    }
}
