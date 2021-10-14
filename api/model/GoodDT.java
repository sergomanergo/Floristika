package ru.kazachkov.florist.api.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public @Data
    class GoodDT implements Parcelable {
    private String flower1CId;
    private double priceAsIs;
    private double soldItems;
    private double priceBought;

    protected GoodDT(Parcel in) {
        flower1CId = in.readString();
        priceAsIs = in.readDouble();
        soldItems = in.readDouble();
        priceBought = in.readDouble();
    }

    public static final Creator<GoodDT> CREATOR = new Creator<GoodDT>() {
        @Override
        public GoodDT createFromParcel(Parcel in) {
            return new GoodDT(in);
        }

        @Override
        public GoodDT[] newArray(int size) {
            return new GoodDT[size];
        }
    };

    public String getPercentage() {
        BigDecimal priceAsIs = new BigDecimal(this.priceAsIs);
        BigDecimal priceBought = new BigDecimal(this.priceBought);
        BigDecimal hundred = new BigDecimal(100);
        return hundred.divide(priceAsIs, 2, RoundingMode.FLOOR).multiply(priceAsIs.subtract(priceBought)).toString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(flower1CId);
        parcel.writeDouble(priceAsIs);
        parcel.writeDouble(soldItems);
        parcel.writeDouble(priceBought);
    }
}
