package ru.kazachkov.florist.interfaces;

public interface DeleteHandler<T> {
    void selectItem(int position);

    void undoDeleteAction();

    void deleteSelected();
}
