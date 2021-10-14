package ru.kazachkov.florist.api.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import ru.kazachkov.florist.interfaces.BindDetailed;
import ru.kazachkov.florist.interfaces.OrderData;

@Builder
@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailedData4 implements OrderData, Parcelable {
    @Builder.Default
    String order1CId = "";
    String order1CDate;
    @Builder.Default
    String buyer1CId = "";
    @Builder.Default
    int docType = -1;
    String storage1CId;
    double orderTotalSum;
    double orderPaidMoney;
    double orderPaidMoneyBank;
    @Builder.Default
    double orderDiscount = 0.0;
    @Builder.Default
    String commentString = "";
    @Builder.Default
    String responciblePersonName = "";
    int isChequed;
    int isFinished;
    int isPrepaid;
    int saleOrderType;
    int isFlowerServiceAuto;
    int orderStatus;
    List<GoodSMDiscDT> goods;

    protected OrderDetailedData4(Parcel in) {
        order1CId = in.readString();
        order1CDate = in.readString();
        buyer1CId = in.readString();
        docType = in.readInt();
        storage1CId = in.readString();
        orderTotalSum = in.readDouble();
        orderPaidMoney = in.readDouble();
        orderPaidMoneyBank = in.readDouble();
        orderDiscount = in.readDouble();
        commentString = in.readString();
        isChequed = in.readInt();
        isFinished = in.readInt();
        isPrepaid = in.readInt();
        saleOrderType = in.readInt();
        isFlowerServiceAuto = in.readInt();
        orderStatus = in.readInt();
        goods = in.createTypedArrayList(GoodSMDiscDT.CREATOR);
    }

    public static final Creator<OrderDetailedData4> CREATOR = new Creator<OrderDetailedData4>() {
        @Override
        public OrderDetailedData4 createFromParcel(Parcel in) {
            return new OrderDetailedData4(in);
        }

        @Override
        public OrderDetailedData4[] newArray(int size) {
            return new OrderDetailedData4[size];
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
        parcel.writeString(buyer1CId);
        parcel.writeInt(docType);
        parcel.writeString(storage1CId);
        parcel.writeDouble(orderTotalSum);
        parcel.writeDouble(orderPaidMoney);
        parcel.writeDouble(orderPaidMoneyBank);
        parcel.writeDouble(orderDiscount);
        parcel.writeString(commentString);
        parcel.writeInt(isChequed);
        parcel.writeInt(isFinished);
        parcel.writeInt(isPrepaid);
        parcel.writeInt(saleOrderType);
        parcel.writeInt(isFlowerServiceAuto);
        parcel.writeInt(orderStatus);
        parcel.writeTypedList(goods);
    }


    public List<GoodDT> convertGoodsToOld(){
        List<GoodDT> list = new ArrayList<>();
        for(GoodSMDiscDT ddt : goods){
            list.add(new GoodDT(ddt.getFlower1CId(), Double.valueOf(ddt.getPriceWODisc()), ddt.getSoldItems(),
                    getPriceWithDisc(ddt.getPriceWODisc(), ddt.getDiscVal())
                    ));
        }
        return list;
    }

    private Double getPriceWithDisc(String priseWODisc, String dicsVal){
        double oPrice = Double.valueOf(priseWODisc);
        double disc = Double.valueOf(dicsVal);
        return oPrice * (1 - disc / 100);
    }

    public static OrderDetailed convertNewOrderToOld(OrderDetailedData4 old) {
        OrderDetailed s = new OrderDetailed();
        s.setOrder1CId(old.getOrder1CId());
        s.setOrderDate(old.getOrder1CDate());
        s.setBuyer1CId(old.getBuyer1CId());
        s.setDocType(old.getDocType());
        s.setStorage1CId(old.getStorage1CId());
        s.setSumFinal(old.getOrderTotalSum());
        s.setPaidMoney(old.getOrderPaidMoney());
        s.setTodayPayment(old.getOrderPaidMoney());
        s.setCommentString(old.getCommentString());
        s.setOrderDiscountValue(old.getOrderDiscount());
        s.setGoods(old.convertGoodsToOld());
        s.setPaidMoneyBank(old.getOrderPaidMoneyBank());
        s.setTodayPaymentBank(old.getOrderPaidMoney());
        s.setIsPrepaid(old.getIsPrepaid());
        s.setPaidStatus(old.getOrderStatus());
        return s;
    }


}
