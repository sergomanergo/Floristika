package ru.kazachkov.florist.api.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ru.kazachkov.florist.interfaces.FromRealmConvert;

public class RealmFakeItem extends RealmObject implements FromRealmConvert<String> {

    @PrimaryKey
    private String fakeItem;

    public RealmFakeItem() {

    }

    public RealmFakeItem(String fakeItem) {
        this.fakeItem = fakeItem;
    }

    @Override
    public String fromRealm() {
        return fakeItem;
    }
}
