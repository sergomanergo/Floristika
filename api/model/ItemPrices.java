package ru.kazachkov.florist.api.model;


import io.realm.RealmObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kazachkov.florist.api.realm.RealmItemPrices;
import ru.kazachkov.florist.interfaces.ToRealmConvert;

@NoArgsConstructor
@AllArgsConstructor
public @Data
class ItemPrices implements ToRealmConvert {
    private String storage1CId;
    private String flower1CId;
    private double regularPrice;
    private double minPrice;

    @Override
    public RealmObject convertToFunc() {
        return RealmItemPrices.newInstance(storage1CId, flower1CId, regularPrice, minPrice);
    }

}
