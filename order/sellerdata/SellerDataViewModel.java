package ru.kazachkov.florist.order.sellerdata;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.math.BigDecimal;

import ru.kazachkov.florist.BR;
import ru.kazachkov.florist.tools.SaleMode;

/**
 * Created by ishmukhametov on 11.10.16.
 */

public class SellerDataViewModel extends BaseObservable {

    private SellerDataContract.Presenter presenter;

    private SaleMode saleMode;
    private String clientName = "";
    private String phoneNumber = "";
    private boolean showInfoPanel = true;

    public SellerDataViewModel(SellerDataContract.Presenter presenter) {
        this.presenter = presenter;
        saleMode = SaleMode.SALE;
    }

    @Bindable
    public SaleMode getSaleMode() {
        return saleMode;
    }

    @Bindable
    public boolean getShowInfoPanel() {
        return showInfoPanel;
    }

    public void setShowInfoPanel(boolean showInfoPanel) {
        this.showInfoPanel = showInfoPanel;
        notifyPropertyChanged(BR.showInfoPanel);
    }

    @Bindable
    public String getClientSellerName() {
        return clientName;
    }

    @Bindable
    public String getClientPhoneNumber() {
        return phoneNumber; //"+7 909 803 1919";
    }

    @Bindable
    public String getRetailPrice() {
        return "1 290 р.";
    }

    @Bindable
    public String getBasePrice() {
        return presenter.getBasePrice();
    }

    public void setBasePrice(BigDecimal value) {
        presenter.setBasePrice(value);
        notifyPropertyChanged(BR.basePrice);
    }

    public void setSaleMode(SaleMode saleMode) {
        this.saleMode = saleMode;
        presenter.setSaleMode(saleMode);
        notifyPropertyChanged(BR.saleMode);
    }

    public void setClientName(String name) {
        this.clientName = name;
        notifyPropertyChanged(BR.clientSellerName);
    }

    public void setClientPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        notifyPropertyChanged(BR.clientPhoneNumber);
    }
}
