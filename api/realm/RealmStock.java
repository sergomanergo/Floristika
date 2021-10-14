package ru.kazachkov.florist.api.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ru.kazachkov.florist.api.model.Stock;
import ru.kazachkov.florist.interfaces.FromRealmConvert;


public class RealmStock extends RealmObject implements FromRealmConvert<Stock> {

    @PrimaryKey
    private String id;
    private String storage1CId;
    private String flower1CId;
    private double stockItems;
    private int inStock;


    public static RealmStock newInstance(String storage1CId, String flower1CId, double stockItems, int inStock) {
        RealmStock realmStock = new RealmStock();
        realmStock.setStorage1CId(storage1CId);
        realmStock.setFlower1CId(flower1CId);
        realmStock.setStockItems(stockItems);
        realmStock.setInStock(inStock);
        realmStock.setId(storage1CId + flower1CId);
        return realmStock;
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

    public double getStockItems() {
        return stockItems;
    }

    public void setStockItems(double stockItems) {
        this.stockItems = stockItems;
    }

    public int getInStock() {
        return inStock;
    }

    public void setInStock(int inStock) {
        this.inStock = inStock;
    }

    @Override
    public Stock fromRealm() {
        return new Stock(storage1CId, flower1CId, stockItems, inStock);
    }
}
