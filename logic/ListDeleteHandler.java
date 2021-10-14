package ru.kazachkov.florist.logic;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.kazachkov.florist.interfaces.DeleteHandler;
import ru.kazachkov.florist.interfaces.ListParent;

public class ListDeleteHandler<T> implements DeleteHandler<T> {

    private final ListParent<T> parent;

    private List<Integer> selected = new ArrayList<>();
    private Map<Integer, T> deletedItems = new HashMap<>();

    public int select(int position) {
        if (selected.contains(position)) {
            selected.remove(Integer.valueOf(position));
        } else {
            selected.add(position);
        }
        return selected.size();
    }


    public ListDeleteHandler(ListParent<T> parent) {
        this.parent = parent;
    }

    @Override
    public void selectItem(int position) {
        select(position);
        parent.itemSelected(this, position, selected.size());
    }

    @Override
    public void undoDeleteAction() {
        for (Map.Entry<Integer, T> entry : deletedItems.entrySet()) {
            parent.addItem(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void deleteSelected() {
        for (Integer pos : selected) {
            T t = parent.remove(pos);
            deletedItems.put(pos, t);
            parent.update(pos);
        }
        parent.itemDeleted(this);
    }
}
