package ru.kazachkov.florist.data.adapters;

import android.content.ContentValues;

import lombok.AllArgsConstructor;
import ru.kazachkov.florist.api.model.Stock;
import ru.kazachkov.florist.data.Contract;
import ru.kazachkov.florist.interfaces.DbAdapter;
import ru.kazachkov.florist.tools.BriteContentValue;
import rx.Observable;


@AllArgsConstructor
public class StockAdapter implements DbAdapter {
    Stock stock;

    @Override
    public String getTableName() {
        return Contract.Stock.TABLE_NAME;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues(4);
        contentValues.put(Contract.Stock.COLUMN_FLOWER_ID, stock.getFlower1CId());
        contentValues.put(Contract.Stock.COLUMN_IN_STOCK, stock.getInStock());
        contentValues.put(Contract.Stock.COLUMN_ITEMS, stock.getStockItems());
        contentValues.put(Contract.Stock.COLUMN_STORAGE_ID, stock.getStorage1CId());
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
    public Object getModel() {
        return null;
    }

}
