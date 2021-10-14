package ru.kazachkov.florist.data.adapters;

import android.content.ContentValues;

import lombok.AllArgsConstructor;
import ru.kazachkov.florist.api.model.Item;
import ru.kazachkov.florist.data.Contract;
import ru.kazachkov.florist.interfaces.DbAdapter;
import ru.kazachkov.florist.tools.BriteContentValue;
import rx.Observable;


@AllArgsConstructor
public class ItemAdapter implements DbAdapter<Item> {
    Item item;

    @Override
    public String getTableName() {
        return Contract.Item.TABLE_NAME;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues(6);
        contentValues.put(Contract.Item.COLUMN_ID, item.getFlower1CId());
        contentValues.put(Contract.Item.COLUMN_NAME, item.getFlowerName());
        contentValues.put(Contract.Item.COLUMN_ARTICLE, item.getFlowerArticle());
        contentValues.put(Contract.Item.COLUMN_UNITS_ID, item.getFlowerArticle());
        contentValues.put(Contract.Item.COLUMN_IS_LOCKED, item.getIsLockEd());
        contentValues.put(Contract.Item.COLUMN_IS_ZERO_STOCK, item.getIsZeroStock());
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
    public Item getModel() {
        return item;
    }

}
