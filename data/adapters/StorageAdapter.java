package ru.kazachkov.florist.data.adapters;

import android.content.ContentValues;

import lombok.AllArgsConstructor;
import ru.kazachkov.florist.data.Contract;
import ru.kazachkov.florist.interfaces.DbAdapter;
import ru.kazachkov.florist.tools.BriteContentValue;
import rx.Observable;


@AllArgsConstructor
public class StorageAdapter implements DbAdapter<ru.kazachkov.florist.api.model.Storage> {
    ru.kazachkov.florist.api.model.Storage storage;

    @Override
    public String getTableName() {
        return Contract.Storage.TABLE_NAME;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues(2);
        contentValues.put(Contract.Storage.COLUMN_ID, storage.getStorage1CId());
        contentValues.put(Contract.Storage.COLUMN_NAME, storage.getStorageName());
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
    public ru.kazachkov.florist.api.model.Storage getModel() {
        return storage;
    }

}
