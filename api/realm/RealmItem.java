package ru.kazachkov.florist.api.realm;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ru.kazachkov.florist.api.model.Item;
import ru.kazachkov.florist.interfaces.FromRealmConvert;

public class RealmItem extends RealmObject implements FromRealmConvert<Item> {

    @PrimaryKey
    private String flower1CId;
    private String flowerName;
    private String flowerArticle;
    private String edIzmId;
    private int isLockEd;
    private int isZeroStock;

    public static RealmItem newInstance(String flower1CId, String flowerName, String flowerArticle, String edIzmId, int isLockEd, int isZeroStock) {
        RealmItem realmItem = new RealmItem();
        realmItem.setFlower1CId(flower1CId);
        realmItem.setFlowerName(flowerName);
        realmItem.setFlowerArticle(flowerArticle);
        realmItem.setEdIzmId(edIzmId);
        realmItem.setIsLockEd(isLockEd);
        realmItem.setIsZeroStock(isZeroStock);
        return realmItem;
    }

    public String getFlower1CId() {
        return flower1CId;
    }

    public void setFlower1CId(String flower1CId) {
        this.flower1CId = flower1CId;
    }

    public String getFlowerName() {
        return flowerName;
    }

    public void setFlowerName(String flowerName) {
        this.flowerName = flowerName;
    }

    public String getFlowerArticle() {
        return flowerArticle;
    }

    public void setFlowerArticle(String flowerArticle) {
        this.flowerArticle = flowerArticle;
    }

    public String getEdIzmId() {
        return edIzmId;
    }

    public void setEdIzmId(String edIzmId) {
        this.edIzmId = edIzmId;
    }

    public int getIsLockEd() {
        return isLockEd;
    }

    public void setIsLockEd(int isLockEd) {
        this.isLockEd = isLockEd;
    }

    public int getIsZeroStock() {
        return isZeroStock;
    }

    public void setIsZeroStock(int isZeroStock) {
        this.isZeroStock = isZeroStock;
    }

    @Override
    public Item fromRealm() {
        return new Item(flower1CId, flowerName, flowerArticle, edIzmId, isLockEd, isZeroStock);
    }
}
