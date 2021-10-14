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
@AllArgsConstructor
@lombok.Data
@NoArgsConstructor
public class OrderDetailed implements Parcelable, BindDetailed {
    @Element(name = "order1CId")
    @Builder.Default
    String order1CId = "";

    @Element(name = "orderDate")
    String orderDate;

    @Element(name = "buyer1CId")
    @Builder.Default
    String buyer1CId = "";

    @Element(name = "docType")
    @Builder.Default
    int docType = -1;

    @Element(name = "storage1CId")
    String storage1CId;

    @Element(name = "sumFinal")
    @Builder.Default
    double sumFinal = 0.0;

    @Element(name = "paidStatus")
    @Builder.Default
    int paidStatus = 0;

    @Element(name = "paidMoney")
    @Builder.Default
    double paidMoney = 0.0;

    @Element(name = "todayPayment")
    @Builder.Default
    double todayPayment = 0.0;

    @Element(name = "commentString")
    @Builder.Default
    String commentString = "";

    @Element(name = "orderDiscountValue")
    @Builder.Default
    double orderDiscountValue = 0.0;

    @Element(name = "saleOrderType")
    @Builder.Default
    int saleOrderType = 0;

    @Path("goods")
    @ElementList(inline = true, entry = "GoodDT")
    List<GoodDT> goods;

    @Element(name = "user1CId")
    @Builder.Default
    String user1CId = "";

    @Element(name = "paidMoneyBank")
    @Builder.Default
    double paidMoneyBank = 0.0;

    @Element(name = "todayPaymentBank")
    @Builder.Default
    double todayPaymentBank = 0.0;

    @Element(name = "isPrepaid")
    @Builder.Default
    int isPrepaid = 0;


    protected OrderDetailed(Parcel in) {
        order1CId = in.readString();
        orderDate = in.readString();
        buyer1CId = in.readString();
        docType = in.readInt();
        storage1CId = in.readString();
        sumFinal = in.readDouble();
        paidStatus = in.readInt();
        paidMoney = in.readDouble();
        todayPayment = in.readDouble();
        commentString = in.readString();
        orderDiscountValue = in.readDouble();
        saleOrderType = in.readInt();
        goods = in.createTypedArrayList(GoodDT.CREATOR);
        user1CId = in.readString();
        paidMoneyBank = in.readDouble();
        todayPaymentBank = in.readDouble();
        isPrepaid = in.readInt();
    }

    public static final Creator<OrderDetailed> CREATOR = new Creator<OrderDetailed>() {
        @Override
        public OrderDetailed createFromParcel(Parcel in) {
            return new OrderDetailed(in);
        }

        @Override
        public OrderDetailed[] newArray(int size) {
            return new OrderDetailed[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(order1CId);
        parcel.writeString(orderDate);
        parcel.writeString(buyer1CId);
        parcel.writeInt(docType);
        parcel.writeString(storage1CId);
        parcel.writeDouble(sumFinal);
        parcel.writeInt(paidStatus);
        parcel.writeDouble(paidMoney);
        parcel.writeDouble(todayPayment);
        parcel.writeString(commentString);
        parcel.writeDouble(orderDiscountValue);
        parcel.writeInt(saleOrderType);
        parcel.writeTypedList(goods);
        parcel.writeString(user1CId);
        parcel.writeDouble(paidMoneyBank);
        parcel.writeDouble(todayPaymentBank);
        parcel.writeInt(isPrepaid);
    }

    @Override
    public boolean isPurchase() {
        return false;
    }

    @Override
    public boolean isPrepaid() {
        return isPrepaid == 1;
    }

    @Override
    public void sumFinal(double sum) {
        sumFinal = sum;
    }
}
