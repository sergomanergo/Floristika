package ru.kazachkov.florist.api.model;

import io.realm.RealmObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.kazachkov.florist.api.realm.RealmItemCategory;
import ru.kazachkov.florist.interfaces.ToRealmConvert;

@AllArgsConstructor
public @Data
class ItemCategory implements ToRealmConvert {
    private final String category1CId;
    private final String flower1CId;

    @Override
    public RealmObject convertToFunc() {
        return RealmItemCategory.newInstance(category1CId, flower1CId);
    }
}
