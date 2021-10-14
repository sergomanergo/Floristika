package ru.kazachkov.florist.api.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.joda.time.DateTime;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.kazachkov.florist.interfaces.IOrderGlance;
import ru.kazachkov.florist.logic.PaidHintData;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderGlance implements Parcelable, IOrderGlance {
    String order1CId;
    String order1CDate;
    int docType;
    String sumFinal;
    int paidStatus;
    String paidStatusHint;
    String itemString;
    String commentString;
    int orderSeqNum;
    String paidSumCashTotal;
    String paidSumCashToday;
    String paidSumBankTotal;
    String paidSumBankToday;
    String responciblePersonName;

    String storage1CId;

    String secondPart1CId;
    String secondPartName;

    int isChequed;
    int isPrepaid;
    int isSold;
    int webShopStatus;
    List<WtdAction> wtdActionsList;
    int position;

    public int getGroup() {
        switch (paidStatus) {
            case 0:
                return 1;
            case 110:
                return 4;
            case 5:
            case 10:
            case 15:
            case 25:
                return 3;
            case 30:
            case 35:
            case 40:
            case 45:
            case 50:
            case 55:
            case 60:
            case 65:
            case 70:
            case 75:
                return 2;
            default:
                return 1;
        }
    }

    @Override
    public boolean isPurchase() {
        return false;
    }

    @Override
    public PaidHintData getPaidDataHint() {
        return new PaidHintData(sumFinal, paidSumCashTotal, paidSumCashToday, paidSumBankTotal, paidSumBankToday);
    }

    @Override
    public void setComment(String comment) {
        commentString = comment;
    }


    private OrderGlance(Parcel in) {
        order1CId = in.readString();
        order1CDate = in.readString();
        docType = in.readInt();
        sumFinal = in.readString();
        paidStatus = in.readInt();
        paidStatusHint = in.readString();
        itemString = in.readString();
        commentString = in.readString();
        orderSeqNum = in.readInt();
        paidSumBankTotal = in.readString();
        paidSumCashTotal = in.readString();
        responciblePersonName = in.readString();
        storage1CId = in.readString();
        secondPart1CId = in.readString();
        secondPartName = in.readString();
        isChequed = in.readInt();
        isPrepaid = in.readInt();
        isSold = in.readInt();
        webShopStatus = in.readInt();
        wtdActionsList = in.createTypedArrayList(WtdAction.CREATOR);

        Log.d("nev","id=" + order1CId);
        Log.d("nev","date=" + order1CDate);
        Log.d("nev","stor=" + storage1CId);
    }

    public static final Creator<OrderGlance> CREATOR = new Creator<OrderGlance>() {
        @Override
        public OrderGlance createFromParcel(Parcel in) {
            return new OrderGlance(in);
        }

        @Override
        public OrderGlance[] newArray(int size) {
            return new OrderGlance[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(order1CId);
        parcel.writeString(order1CDate);
        parcel.writeInt(docType);
        parcel.writeString(sumFinal);
        parcel.writeInt(paidStatus);
        parcel.writeString(paidStatusHint);
        parcel.writeString(itemString);
        parcel.writeString(commentString);
        parcel.writeInt(orderSeqNum);
        parcel.writeString(paidSumBankTotal);
        parcel.writeString(paidSumCashTotal);
        parcel.writeString(responciblePersonName);
        parcel.writeString(storage1CId);
        parcel.writeString(storage1CId);
        parcel.writeString(secondPartName);
        parcel.writeInt(isChequed);
        parcel.writeInt(isPrepaid);
        parcel.writeInt(isSold);
        parcel.writeInt(webShopStatus);
        parcel.writeTypedList(wtdActionsList);
    }

    public String getOrder1CDate() {
        Pattern pattern = Pattern.compile("^/Date\\(?([0-9]*)\\)/$");
        Matcher m = pattern.matcher(order1CDate);
        if (m.find()) {
            return new DateTime(Long.valueOf(m.group(1))).toString();
        }
        return order1CDate;
    }

    @Override
    public String getClientName() {
        return secondPartName;
    }

    @Override
    public String getClientId() {
        return secondPart1CId;
    }


}
