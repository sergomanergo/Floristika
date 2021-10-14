package ru.kazachkov.florist.api.model;

import io.realm.RealmObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.kazachkov.florist.api.realm.RealmStorage;
import ru.kazachkov.florist.interfaces.ToRealmConvert;

@AllArgsConstructor
public @Data
class Storage implements ToRealmConvert {
    private final String storage1CId;
    private final String storageName;

    @Override
    public RealmObject convertToFunc() {
        return RealmStorage.newInstance(storage1CId, storageName);
    }
}
