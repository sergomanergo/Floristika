package ru.kazachkov.florist.sales;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.text.TextUtils;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.text.NumberFormat;

import ru.kazachkov.florist.tools.Utils;

/**
 * Created by ishmukhametov on 20.10.16.
 */

public class OrderDataViewModel extends BaseObservable {


    private String comment;

    private OrderDataDialogInfo orderDataDialogInfo;

    public OrderDataViewModel(OrderDataDialogInfo orderDataDialogInfo) {
        this.orderDataDialogInfo = orderDataDialogInfo;
        comment = orderDataDialogInfo.getComment();
    }

    @Bindable
    public String getClientInfo() {
        return String.format("%s\n%s\nСкидка %s ",
                orderDataDialogInfo.getClientName(),
                orderDataDialogInfo.getClientNumber(),
                orderDataDialogInfo.getDiscountValue()) + "%";
    }

    @Bindable
    public String getFloristInfo() {
        String floristName = orderDataDialogInfo.getFloristName();
        DateTime dateTime = orderDataDialogInfo.getOrderDate();
        String dateString = Utils.prepareDate(dateTime.toString(), "dd MMMM YYYY");
        String orderId = orderDataDialogInfo.getOrderId();
        return String.format("%s\n%s\n%s", floristName, dateString, orderId);
    }

    @Bindable
    public String getCompositionInfo() {
        return orderDataDialogInfo.getCompositionInfo();
    }

    @Bindable
    public String getComment() {
        return comment;
    }


    @Bindable
    public String getBalance() {
        BigDecimal paidSum = orderDataDialogInfo.getPaidSum();
        BigDecimal totalSum = orderDataDialogInfo.getTotalSum();
        NumberFormat currencyFormat = Utils.getCurrencyFormat();
        return String.format("остаток – %s р.", currencyFormat.format(totalSum.subtract(paidSum)));
    }

    @Bindable
    public String getPayment() {
        BigDecimal paidSum = orderDataDialogInfo.getPaidSum();
        BigDecimal totalSum = orderDataDialogInfo.getTotalSum();
        NumberFormat currencyFormat = Utils.getCurrencyFormat();
        return String.format("%s из  %s р.", currencyFormat.format(paidSum), currencyFormat.format(totalSum));
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean commentChanged() {
        return !TextUtils.equals(comment, orderDataDialogInfo.getComment());
    }
}
