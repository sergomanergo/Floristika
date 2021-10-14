package ru.kazachkov.florist.api.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ru.kazachkov.florist.api.model.ItemPrices;
import ru.kazachkov.florist.interfaces.FromRealmConvert;


public class RealmItemPrices extends RealmObject implements FromRealmConvert<ItemPrices> {

    @PrimaryKey
    private String id;
    private String storage1CId;
    private String flower1CId;
    private double regularPrice;
    private double minPrice;

    public static RealmItemPrices newInstance(String storage1CId, String flower1CId, double regularPrice, double minPrice) {
        RealmItemPrices realmItemPrices = new RealmItemPrices();
        realmItemPrices.setStorage1CId(storage1CId);
        realmItemPrices.setFlower1CId(flower1CId);
        realmItemPrices.setRegularPrice(regularPrice);
        realmItemPrices.setMinPrice(minPrice);
        realmItemPrices.setId(storage1CId + flower1CId);
        return realmItemPrices;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStorage1CId() {
        return storage1CId;
    }

    public void setStorage1CId(String storage1CId) {
        this.storage1CId = storage1CId;
        this.id = storage1CId + flower1CId;
    }

    public String getFlower1CId() {
        return flower1CId;
    }

    public void setFlower1CId(String flower1CId) {
        this.flower1CId = flower1CId;
        this.id = storage1CId + flower1CId;
    }

    public double getRegularPrice() {
        return regularPrice;
    }

    public void setRegularPrice(double regularPrice) {
        this.regularPrice = regularPrice;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    @Override
    public ItemPrices fromRealm() {
        return new ItemPrices(storage1CId, flower1CId, regularPrice, minPrice);
    }
}
