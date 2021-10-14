package ru.kazachkov.florist.data.adapters;

import android.content.ContentValues;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import ru.kazachkov.florist.api.model.FastUpdate;
import ru.kazachkov.florist.interfaces.DbAdapter;
import ru.kazachkov.florist.tools.BriteContentValue;
import rx.Observable;


@AllArgsConstructor
public class FastUpdateAdapter implements DbAdapter<FastUpdate> {
    FastUpdate fastUpdate;

    @Override
    public String getTableName() {
        return null;
    }

    @Override
    public ContentValues getContentValues() {
        return null;
    }

    @Override
    public BriteContentValue getBriteContentValue() {
        return null;
    }

    @Override
    public Observable<BriteContentValue> getBriteContentValueObs() {
        return Observable.from(fastUpdate.getEdIzmArray() != null ? fastUpdate.getEdIzmArray() : new ArrayList<>())
                .flatMap(edIzm -> new EdIzmAdapter(edIzm).getBriteContentValueObs());
    }

    @Override
    public FastUpdate getModel() {
        return fastUpdate;
    }

}
