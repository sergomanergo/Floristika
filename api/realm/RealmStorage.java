package ru.kazachkov.florist.api.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ru.kazachkov.florist.api.model.Storage;
import ru.kazachkov.florist.interfaces.FromRealmConvert;

public class RealmStorage extends RealmObject implements FromRealmConvert<Storage> {
    @PrimaryKey
    private String storage1CId;
    private String storageName;

    public static RealmStorage newInstance(String storage1CId, String storageName) {
        RealmStorage realmStorage = new RealmStorage();
        realmStorage.setStorageName(storageName);
        realmStorage.setStorage1CId(storage1CId);
        return realmStorage;
    }

    public String getStorage1CId() {
        return storage1CId;
    }

    public void setStorage1CId(String storage1CId) {
        this.storage1CId = storage1CId;
    }

    public String getStorageName() {
        return storageName;
    }

    public void setStorageName(String storageName) {
        this.storageName = storageName;
    }

    @Override
    public Storage fromRealm() {
        return new Storage(storage1CId, storageName);
    }
}
