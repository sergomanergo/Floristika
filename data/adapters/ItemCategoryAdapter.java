package ru.kazachkov.florist.data.adapters;

import android.content.ContentValues;

import lombok.AllArgsConstructor;
import ru.kazachkov.florist.api.model.ItemCategory;
import ru.kazachkov.florist.data.Contract;
import ru.kazachkov.florist.interfaces.DbAdapter;
import ru.kazachkov.florist.tools.BriteContentValue;
import rx.Observable;


@AllArgsConstructor
public class ItemCategoryAdapter implements DbAdapter<ItemCategory> {
    ItemCategory itemCategory;

    @Override
    public String getTableName() {
        return Contract.ItemCategories.TABLE_NAME;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues(2);
        contentValues.put(Contract.ItemCategories.COLUMN_CAT_ID, itemCategory.getCategory1CId());
        contentValues.put(Contract.ItemCategories.COLUMN_ITEM_ID, itemCategory.getFlower1CId());
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
    public ItemCategory getModel() {
        return itemCategory;
    }

}
