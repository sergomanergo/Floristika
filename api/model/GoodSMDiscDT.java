package ru.kazachkov.florist.api.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.Getter;


public class GoodSMDiscDT implements Parcelable {
    private String flower1CId;
    private String priceWODisc;
    private String discVal;
    @Getter
    private double soldItems;

    public GoodSMDiscDT() {
    }

    public GoodSMDiscDT(String flower1CId, String priceWODisc, String discVal, double soldItems) {
        this.flower1CId = flower1CId;
        this.priceWODisc = priceWODisc;
        this.discVal = discVal;
        this.soldItems = soldItems;
    }

    public GoodSMDiscDT(GoodDT goodDT) {
        this.flower1CId = goodDT.getFlower1CId();
        this.priceWODisc = new BigDecimal(goodDT.getPriceAsIs()).toString();
        this.discVal = goodDT.getPercentage();
        this.soldItems = goodDT.getSoldItems();
    }

    public GoodSMDiscDT(GoodDT goodDT, BigDecimal price) {
        this.flower1CId = goodDT.getFlower1CId();
        //this.priceWODisc = price.toString();
        this.priceWODisc = BigDecimal.valueOf(goodDT.getPriceAsIs()).setScale(2, RoundingMode.UP).toString();
        goodDT.setPriceBought(price.doubleValue());
        this.discVal = goodDT.getPercentage();
        this.soldItems = goodDT.getSoldItems();
    }

    public GoodDT asGoodDT() {
        return new GoodDT(this.getFlower1CId(), 0/*getPriceWODisc()*/, getSoldItems(), 0);
    }

    public String getFlower1CId() {
        return flower1CId;
    }

    public void setFlower1CId(String flower1CId) {
        this.flower1CId = flower1CId;
    }

    public String getPriceWODisc() {
        return priceWODisc;
    }

    public void setPriceWODisc(String priceWODisc) {
        this.priceWODisc = priceWODisc;
    }

    public String getDiscVal() {
        return discVal;
    }

    public void setDiscVal(String discVal) {
        this.discVal = discVal;
    }

    protected GoodSMDiscDT(Parcel in) {
        flower1CId = in.readString();
        priceWODisc = in.readString();
        discVal = in.readString();
        soldItems = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(flower1CId);
        dest.writeString(priceWODisc);
        dest.writeString(discVal);
        dest.writeDouble(soldItems);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GoodSMDiscDT> CREATOR = new Creator<GoodSMDiscDT>() {
        @Override
        public GoodSMDiscDT createFromParcel(Parcel in) {
            return new GoodSMDiscDT(in);
        }

        @Override
        public GoodSMDiscDT[] newArray(int size) {
            return new GoodSMDiscDT[size];
        }
    };
}
