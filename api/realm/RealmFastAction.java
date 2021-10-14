package ru.kazachkov.florist.api.realm;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ru.kazachkov.florist.api.model.FastAction;
import ru.kazachkov.florist.interfaces.FromRealmConvert;


public class RealmFastAction extends RealmObject implements FromRealmConvert<FastAction> {
    @PrimaryKey
    private int id;
    private String name;

    public RealmFastAction() {
    }

    public static RealmFastAction newInstance(int id, String name) {
        RealmFastAction realmFastAction = new RealmFastAction();
        realmFastAction.setId(id);
        realmFastAction.setName(name);
        return realmFastAction;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public FastAction fromRealm() {
        FastAction fastAction = new FastAction();
        fastAction.setId(id);
        fastAction.setName(name);
        return fastAction;
    }
}
