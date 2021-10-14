package ru.kazachkov.florist.api.model;

import io.realm.RealmObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.kazachkov.florist.api.realm.RealmCategory;
import ru.kazachkov.florist.interfaces.SpinnerText;
import ru.kazachkov.florist.interfaces.ToRealmConvert;


@AllArgsConstructor
public @Data
class   Category implements ToRealmConvert, SpinnerText {
    private final String category1CId;
    private final String categoryName;

    @Override
    public RealmObject convertToFunc() {
        return RealmCategory.newInstance(category1CId, categoryName);
    }

    public long getId() {
        return category1CId.hashCode();
    }

    @Override
    public String getSpinnerText() {
        return categoryName;
    }
}
