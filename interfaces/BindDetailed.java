package ru.kazachkov.florist.interfaces;


import java.util.List;

import ru.kazachkov.florist.api.model.GoodDT;

public interface BindDetailed extends OrderData {
    List<GoodDT> getGoods();

    double getSumFinal();

    int getDocType();

    boolean isPurchase();

    boolean isPrepaid();

    void sumFinal(double sum);

}
