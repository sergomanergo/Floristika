package ru.kazachkov.florist.data.adapters;

import android.content.ContentValues;

import lombok.AllArgsConstructor;
import ru.kazachkov.florist.api.model.EdIzm;
import ru.kazachkov.florist.data.Contract;
import ru.kazachkov.florist.interfaces.DbAdapter;
import ru.kazachkov.florist.tools.BriteContentValue;
import rx.Observable;


@AllArgsConstructor
public class EdIzmAdapter implements DbAdapter<EdIzm> {
    EdIzm edIzm;

    @Override
    public String getTableName() {
        return Contract.Units.TABLE_NAME;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues(3);
        contentValues.put(Contract.Units.COLUMN_ID, edIzm.getId());
        contentValues.put(Contract.Units.COLUMN_MIN_VALUE, edIzm.getEdIzmMinVal());
        contentValues.put(Contract.Units.COLUMN_NAME, edIzm.getEdIzmName());
        return contentValues;
    }

    @Override
    public BriteContentValue getBriteContentValue() {
        return BriteContentValue.of(getTableName(), getContentValues());
    }

    @Override
    public Observable<BriteContentValue> getBriteContentValueObs() {
        return Observable.just(getBriteContentValue());
    }

    @Override
    public EdIzm getModel() {
        return edIzm;
    }

}
