package ru.kazachkov.florist.model.md;


import android.app.Activity;
import android.databinding.ObservableDouble;

import ru.kazachkov.florist.api.model.OrderDetailed;
import ru.kazachkov.florist.interfaces.PhotoContainer;
import ru.kazachkov.florist.viewmodel.vm.NewOrderVM;

public interface NewOrderScreenModel extends NewOrderVM, PhotoContainer {
    void onNextType();

    void setProgress(int progress);

    void setComment(String comment);

    void setSumFinal(double sum);

    void onCheckoutOrder();

    void setDiscount(Double aDouble);

    ObservableDouble getPaidValue();

    void onAddDiscount();

    void onDeductDiscount();

    Activity getContext();

    void setBayer(int position, long id);

    void setAuthor(int position, long id);

    void bind(OrderDetailed orderDetailed);

    void setOrderDocType(int i);

    void setBankValue(int i);

    void setPaidValue(double value);

    void setComebackValue(double value);


    void setSeller(int i, long l);

    void onAddNewBayer(String s, boolean isBayer, boolean isSeller);

    String getOrderId();

    void onAddNewSeller(String s, Boolean aBoolean);
}
