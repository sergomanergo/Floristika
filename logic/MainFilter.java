package ru.kazachkov.florist.logic;

import ru.kazachkov.florist.interfaces.Filterable;

/**
 * Created by ishmukhametov on 23.03.16.
 * The class includes information for filtering data on the MainActivity.
 */
public class MainFilter implements Filterable {

    @Override
    public long getDate() {
        return 0;
    }

    @Override
    public int getType() {
        return 0;
    }
}
