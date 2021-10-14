package ru.kazachkov.florist.interfaces;


public interface ListParent<T> {
    void itemSelected(DeleteHandler deleteHandler, int pos, int countSelected);

    void itemDeleted(DeleteHandler deleteHandler);

    void addItem(int pos, T t);

    T remove(int pos);

    void update(int pos);
}
