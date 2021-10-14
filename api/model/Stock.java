package ru.kazachkov.florist.api.model;

import io.realm.RealmObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kazachkov.florist.api.realm.RealmStock;
import ru.kazachkov.florist.interfaces.ToRealmConvert;

@NoArgsConstructor
@AllArgsConstructor
public @Data
class Stock implements ToRealmConvert {
    private String storage1CId;
    private String flower1CId;
    private double stockItems;
    private int inStock;

    @Override
    public RealmObject convertToFunc() {
        return RealmStock.newInstance(storage1CId, flower1CId, stockItems, inStock);
    }
}
