package ru.kazachkov.florist.api.realm;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Getter;
import lombok.Setter;
import ru.kazachkov.florist.api.model.FastAction;
import ru.kazachkov.florist.api.model.OrderFastActions;
import ru.kazachkov.florist.interfaces.FromRealmConvert;

/**
 * Created by ishmukhametov on 08.11.16.
 */

public class RealmOrderFastActions extends RealmObject implements FromRealmConvert<OrderFastActions> {

    @PrimaryKey
    @Getter
    @Setter
    int typeId;
    @Getter
    @Setter
    String typeName;
    @Getter
    @Setter
    RealmList<RealmFastAction> realmFastActions;

    public static RealmOrderFastActions newInstance(int typeId, String typeName, List<FastAction> fastActions) {
        RealmOrderFastActions realmOrderFastActions = new RealmOrderFastActions();
        realmOrderFastActions.setTypeId(typeId);
        realmOrderFastActions.setTypeName(typeName);

        RealmList<RealmFastAction> realmFastActions = new RealmList<>();
        for (FastAction fastAction : fastActions) {
            if (fastAction != null && !TextUtils.isEmpty(fastAction.getName())) {
                realmFastActions.add(fastAction.convertToFunc());
            }
        }
        realmOrderFastActions.setRealmFastActions(realmFastActions);
        return realmOrderFastActions;
    }


    @Override
    public OrderFastActions fromRealm() {
        OrderFastActions orderFastActions = new OrderFastActions();
        orderFastActions.setTypeId(typeId);
        orderFastActions.setTypeName(typeName);
        List<FastAction> fastActions = new ArrayList<>();
        for (RealmFastAction fastAction : realmFastActions) {
            fastActions.add(fastAction.fromRealm());
        }
        orderFastActions.setFastActions(fastActions);
        return orderFastActions;
    }
}
