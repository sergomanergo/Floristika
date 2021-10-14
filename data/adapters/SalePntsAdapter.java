package ru.kazachkov.florist.data.adapters;

import android.content.ContentValues;

import lombok.AllArgsConstructor;
import ru.kazachkov.florist.api.model.UserAuthRole;
import ru.kazachkov.florist.data.Contract;
import ru.kazachkov.florist.interfaces.DbAdapter;
import ru.kazachkov.florist.tools.BriteContentValue;
import rx.Observable;


@AllArgsConstructor
public class SalePntsAdapter implements DbAdapter<UserAuthRole.SalePnt> {
    UserAuthRole.SalePnt salePnt;

    @Override
    public String getTableName() {
        return Contract.SalePnts.TABLE_NAME;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues(3);
        contentValues.put(Contract.SalePnts.COLUMN_ID, salePnt.getStorage1CId());
        contentValues.put(Contract.SalePnts.COLUMN_IS_MANAGER, salePnt.getIsManager());
        contentValues.put(Contract.SalePnts.COLUMN_NAME, salePnt.getStorageName());
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
    public UserAuthRole.SalePnt getModel() {
        return salePnt;
    }

}
