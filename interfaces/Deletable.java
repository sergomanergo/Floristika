package ru.kazachkov.florist.interfaces;

import rx.functions.Action1;

/**
 * Created by ishmukhametov on 28.03.16.
 */
public interface Deletable {
    int select(int position);

    void deleteFromList(Action1<Deletable> deletableAction1);

    void deleteFromDataBase();

    void undoAction();
}
