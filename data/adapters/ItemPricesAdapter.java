package ru.kazachkov.florist.data.adapters;

import android.content.ContentValues;

import lombok.AllArgsConstructor;
import ru.kazachkov.florist.api.model.ItemPrices;
import ru.kazachkov.florist.data.Contract;
import ru.kazachkov.florist.interfaces.DbAdapter;
import ru.kazachkov.florist.tools.BriteContentValue;
import rx.Observable;


@AllArgsConstructor
public class ItemPricesAdapter implements DbAdapter<ItemPrices> {
    ItemPrices itemPrices;

    @Override
    public String getTableName() {
        return Contract.ItemPrices.TABLE_NAME;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues(4);
        contentValues.put(Contract.ItemPrices.COLUMN_FLOWER_ID, itemPrices.getFlower1CId());
        contentValues.put(Contract.ItemPrices.COLUMN_STORAGE_ID, itemPrices.getStorage1CId());
        contentValues.put(Contract.ItemPrices.COLUMN_MIN_PRICE, itemPrices.getMinPrice());
        contentValues.put(Contract.ItemPrices.COLUMN_REGULAR_PRICE, itemPrices.getRegularPrice());
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
    public ItemPrices getModel() {
        return itemPrices;
    }

}
