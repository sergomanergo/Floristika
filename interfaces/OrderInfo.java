package ru.kazachkov.florist.interfaces;

import java.util.List;

/**
 * Created by ishmukhametov on 01.06.16.
 */
public interface OrderInfo {
    long getTotalOrderSum();

    long getTotalBankPaid();

    long getTotalCashPaid();


    List<IOrderGlance> getOrderList();
}
