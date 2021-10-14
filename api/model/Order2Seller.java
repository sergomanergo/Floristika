package ru.kazachkov.florist.api.model;


import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import ru.kazachkov.florist.interfaces.BindDetailed;

@Builder
@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class Order2Seller implements Parcelable, BindDetailed {
    public static final Creator<Order2Seller> CREATOR = new Creator<Order2Seller>() {
        @Override
        public Order2Seller createFromParcel(Parcel in) {
            return new Order2Seller(in);
        }

        @Override
        public Order2Seller[] newArray(int size) {
            return new Order2Seller[size];
        }
    };
    @Element(name = "order1CId")
    @Builder.Default
    String order1CId = "";
    @Element(name = "orderDate")
    String orderDate;
    @Element(name = "docType")
    @Builder.Default
    int docType = -1;
    @Element(name = "storage1CId", required = false)
    String storage1CId;
    @Element(name = "sumFinal")
    @Builder.Default
    double sumFinal = 0.0;
    @Element(name = "additionalSpends")
    @Builder.Default
    double additionalSpends = 0.0;
    @Element(name = "additionalSpendsType")
    @Builder.Default
    int additionalSpendsType = 0;
    @Element(name = "paidMoney")
    @Builder.Default
    double paidMoney = 0.0;
    @Element(name = "seller1CId")
    String seller1CId;
    @Element(name = "commentString", required = false)
    @Builder.Default
    String commentString = "";
    @Path("goods")
    @ElementList(inline = true, entry = "GoodDT")
    List<GoodDT> goods;
    @Element(name = "isPrepaid")
    @Builder.Default
    int isPrepaid = 0;

    protected Order2Seller(Parcel in) {
        order1CId = in.readString();
        orderDate = in.readString();
        docType = in.readInt();
        storage1CId = in.readString();
        sumFinal = in.readDouble();
        additionalSpends = in.readDouble();
        additionalSpendsType = in.readInt();
        paidMoney = in.readDouble();
        seller1CId = in.readString();
        commentString = in.readString();
        goods = in.createTypedArrayList(GoodDT.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(order1CId);
        parcel.writeString(orderDate);
        parcel.writeInt(docType);
        parcel.writeString(storage1CId);
        parcel.writeDouble(sumFinal);
        parcel.writeDouble(additionalSpends);
        parcel.writeInt(additionalSpendsType);
        parcel.writeDouble(paidMoney);
        parcel.writeString(seller1CId);
        parcel.writeString(commentString);
        parcel.writeTypedList(goods);
    }

    @Override
    public boolean isPurchase() {
        return true;
    }

    @Override
    public String getBuyer1CId() {
        throw new NullPointerException("Order2Seller not have getBuyer1CId");
    }

    /*@Override
    public String getUser1CId() {
        throw new NullPointerException("Order2Seller not have getUser1CId");
    }*/


    @Override
    public boolean isPrepaid() {
        return isPrepaid == 1;
    }

    @Override
    public void sumFinal(double sum) {
        sumFinal = sum;
    }

}
