package ru.kazachkov.florist.api.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Getter;
import lombok.Setter;
import ru.kazachkov.florist.api.model.response.OrdersListAction;
import ru.kazachkov.florist.interfaces.FromRealmConvert;


public class RealmOrderListAction extends RealmObject implements FromRealmConvert<OrdersListAction> {
    @PrimaryKey
    @Getter
    @Setter
    int orderListActionId;
    @Getter
    @Setter
    int orderListSequence;
    @Getter
    @Setter
    String orderListActionName;

    public static RealmOrderListAction newInstance(OrdersListAction ordersListAction) {
        RealmOrderListAction realm = new RealmOrderListAction();
        realm.setOrderListActionId(ordersListAction.getOrderListActionId());
        realm.setOrderListActionName(ordersListAction.getOrderListActionName());
        realm.setOrderListSequence(ordersListAction.getOrderListSequence());
        return realm;
    }


    @Override
    public OrdersListAction fromRealm() {
        return new OrdersListAction(orderListActionId, orderListSequence, orderListActionName);
    }
}
