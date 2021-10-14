package ru.kazachkov.florist.api.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ru.kazachkov.florist.api.model.ItemCategory;
import ru.kazachkov.florist.interfaces.FromRealmConvert;


public class RealmItemCategory extends RealmObject implements FromRealmConvert<ItemCategory> {
    @PrimaryKey
    private String flower1CId;
    private String category1CId;

    public static RealmItemCategory newInstance(String category1CId, String flower1CId) {
        RealmItemCategory realmItemCategory = new RealmItemCategory();
        realmItemCategory.setFlower1CId(flower1CId);
        realmItemCategory.setCategory1CId(category1CId);
        return realmItemCategory;
    }


    public String getCategory1CId() {
        return category1CId;
    }

    public void setCategory1CId(String category1CId) {
        this.category1CId = category1CId;
    }

    public String getFlower1CId() {
        return flower1CId;
    }

    public void setFlower1CId(String flower1CId) {
        this.flower1CId = flower1CId;
    }

    @Override
    public ItemCategory fromRealm() {
        return new ItemCategory(category1CId, flower1CId);
    }
}
