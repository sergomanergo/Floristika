package ru.kazachkov.florist.api.realm;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ru.kazachkov.florist.api.model.UserAuthRole;
import ru.kazachkov.florist.interfaces.FromRealmConvert;


public class RealmSalePnt extends RealmObject implements FromRealmConvert<UserAuthRole.SalePnt> {
    @PrimaryKey
    private String storage1CId;
    private int isManager;
    private String storageName;

    public RealmSalePnt newInstance(String storage1CId, int isManager, String storageName) {
        RealmSalePnt realmSalePnt = new RealmSalePnt();
        realmSalePnt.setStorage1CId(storage1CId);
        realmSalePnt.setIsManager(isManager);
        realmSalePnt.setStorageName(storageName);
        return realmSalePnt;
    }

    public String getStorage1CId() {
        return storage1CId;
    }

    public void setStorage1CId(String storage1CId) {
        this.storage1CId = storage1CId;
    }

    public int getIsManager() {
        return isManager;
    }

    public void setIsManager(int isManager) {
        this.isManager = isManager;
    }

    public String getStorageName() {
        return storageName;
    }

    public void setStorageName(String storageName) {
        this.storageName = storageName;
    }

    @Override
    public UserAuthRole.SalePnt fromRealm() {
        UserAuthRole.SalePnt salePnt = new UserAuthRole.SalePnt();
        salePnt.setStorageName(storageName);
        salePnt.setStorage1CId(storage1CId);
        salePnt.setIsManager(isManager);
        return salePnt;
    }
}
