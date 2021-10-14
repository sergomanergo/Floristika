package ru.kazachkov.florist.api.model;

import io.realm.RealmObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.kazachkov.florist.api.realm.RealmEdIzm;
import ru.kazachkov.florist.interfaces.SpinnerText;
import ru.kazachkov.florist.interfaces.ToRealmConvert;

@Data
@AllArgsConstructor
public class EdIzm implements ToRealmConvert, SpinnerText {
    private final String edIzmName;
    private final String edIzm1CId;
    private final double edIzmMinVal;

    @Override
    public RealmObject convertToFunc() {
        return RealmEdIzm.newInstance(edIzmName, edIzm1CId, edIzmMinVal);
    }

    public long getId() {
        return edIzm1CId.hashCode();
    }

    @Override
    public String getSpinnerText() {
        return edIzmName;
    }
}
