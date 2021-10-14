package ru.kazachkov.florist.api.model;

import android.util.Log;

import io.realm.RealmObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.kazachkov.florist.api.realm.RealmItem;
import ru.kazachkov.florist.interfaces.ToRealmConvert;

@AllArgsConstructor
public @Data
class Item implements ToRealmConvert {
    private final String flower1CId;
    private final String flowerName;
    private final String flowerArticle;
    private final String edIzmId;
    private final int isLockEd;
    private final int isZeroStock;

    @Override
    public RealmObject convertToFunc() {
        Log.d("THREAD", "CON " + Thread.currentThread().getName());
        return RealmItem.newInstance(flower1CId, flowerName, flowerArticle, edIzmId, isLockEd, isZeroStock);
    }
}
