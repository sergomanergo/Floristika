package ru.kazachkov.florist.api.realm;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ru.kazachkov.florist.api.model.EdIzm;
import ru.kazachkov.florist.interfaces.FromRealmConvert;

public class RealmEdIzm extends RealmObject implements FromRealmConvert<EdIzm> {
    @PrimaryKey
    private String edIzm1CId;
    private String edIzmName;
    private double edIzmMinVal;

    public RealmEdIzm() {
    }

    public static RealmEdIzm newInstance(String edIzmName, String edIzm1CId, double edIzmMinVal) {
        RealmEdIzm realmEdIzm = new RealmEdIzm();
        realmEdIzm.setEdIzmName(edIzmName);
        realmEdIzm.setEdIzm1CId(edIzm1CId);
        realmEdIzm.setEdIzmMinVal(edIzmMinVal);
        return realmEdIzm;
    }

    public String getEdIzm1CId() {
        return edIzm1CId;
    }

    public void setEdIzm1CId(String edIzm1CId) {
        this.edIzm1CId = edIzm1CId;
    }

    public String getEdIzmName() {
        return edIzmName;
    }

    public void setEdIzmName(String edIzmName) {
        this.edIzmName = edIzmName;
    }

    public double getEdIzmMinVal() {
        return edIzmMinVal;
    }

    public void setEdIzmMinVal(double edIzmMinVal) {
        this.edIzmMinVal = edIzmMinVal;
    }

    @Override
    public EdIzm fromRealm() {
        return new EdIzm(edIzmName, edIzm1CId, edIzmMinVal);
    }
}
