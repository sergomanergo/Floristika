package ru.kazachkov.florist.data.adapters;

import android.content.ContentValues;

import lombok.AllArgsConstructor;
import ru.kazachkov.florist.api.model.Photo;
import ru.kazachkov.florist.data.Contract;
import ru.kazachkov.florist.interfaces.DbAdapter;
import ru.kazachkov.florist.tools.BriteContentValue;
import rx.Observable;


@AllArgsConstructor
public class PhotoAdapter implements DbAdapter<Photo> {
    Photo photo;

    @Override
    public String getTableName() {
        return Contract.Photos.TABLE_NAME;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues(2);
        contentValues.put(Contract.Photos.COLUMN_PATH, photo.getPath());
        contentValues.put(Contract.Photos.COLUMN_ITEM_ID, photo.getItemId());
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
    public Photo getModel() {
        return photo;
    }

}
