package ru.kazachkov.florist.interfaces;


import android.content.ContentValues;

import ru.kazachkov.florist.tools.BriteContentValue;
import rx.Observable;

public interface DbAdapter<M> {
    String getTableName();

    ContentValues getContentValues();

    BriteContentValue getBriteContentValue();

    Observable<BriteContentValue> getBriteContentValueObs();

    M getModel();
}
