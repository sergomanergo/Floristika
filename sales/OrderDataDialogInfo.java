package ru.kazachkov.florist.sales;

import org.joda.time.DateTime;

import java.math.BigDecimal;

import lombok.Builder;

/**
 * Created by ishmukhametov on 09.12.16.
 */
@Builder
public class OrderDataDialogInfo {

    private final String compositionInfo;
    private final String comment;
    private final BigDecimal paidSum;
    private final BigDecimal totalSum;
    private final String floristName;
    private final DateTime orderDate;
    private final String orderId;
    private final String clientName;
    private final String clientNumber;
    private final int discountValue;

    public String getCompositionInfo() {
        return compositionInfo;
    }

    public String getComment() {
        return comment;
    }

    public BigDecimal getPaidSum() {
        return paidSum;
    }

    public BigDecimal getTotalSum() {
        return totalSum;
    }

    public String getFloristName() {
        return floristName;
    }


    public DateTime getOrderDate() {
        return orderDate;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getClientName() {
        return clientName;
    }


    public String getClientNumber() {
        return clientNumber;
    }

    public int getDiscountValue() {
        return discountValue;
    }
}
