package ru.kazachkov.florist.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kazachkov.florist.api.realm.RealmOrderFastActions;
import ru.kazachkov.florist.interfaces.ToRealmConvert;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderFastActions implements ToRealmConvert {

    @SerializedName("orderTypeId")
    private int typeId;

    @SerializedName("orderTypeName")
    private String typeName;

    @SerializedName("orderFA")
    private List<FastAction> fastActions;


    @Override
    public RealmOrderFastActions convertToFunc() {
        return RealmOrderFastActions.newInstance(typeId, typeName, fastActions);
    }
}
