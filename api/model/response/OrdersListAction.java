package ru.kazachkov.florist.api.model.response;

import io.realm.RealmObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.kazachkov.florist.api.realm.RealmOrderListAction;
import ru.kazachkov.florist.interfaces.SpinnerText;
import ru.kazachkov.florist.interfaces.ToRealmConvert;

/**
 * Created by ishmukhametov on 19.09.16.
 */

@AllArgsConstructor
@NoArgsConstructor
public class OrdersListAction implements ToRealmConvert, SpinnerText {
    @Getter
    int orderListActionId;
    @Getter
    int orderListSequence;
    @Getter
    String orderListActionName;

    @Override
    public RealmObject convertToFunc() {
        return RealmOrderListAction.newInstance(this);
    }

    @Override
    public String getSpinnerText() {
        return orderListActionName;
    }

    @Override
    public String toString() {
        return orderListActionName;
    }
}
