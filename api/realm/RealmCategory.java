package ru.kazachkov.florist.api.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ru.kazachkov.florist.api.model.Category;
import ru.kazachkov.florist.interfaces.FromRealmConvert;


public class RealmCategory extends RealmObject implements FromRealmConvert<Category> {
    @PrimaryKey
    private String category1CId;
    private String categoryName;

    public static RealmCategory newInstance(String category1CId, String categoryName) {
        RealmCategory realmCategory = new RealmCategory();
        realmCategory.setCategory1CId(category1CId);
        realmCategory.setCategoryName(categoryName);
        return realmCategory;
    }

    public String getCategory1CId() {
        return category1CId;
    }

    public void setCategory1CId(String category1CId) {
        this.category1CId = category1CId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public Category fromRealm() {
        return new Category(category1CId, categoryName);
    }
}
