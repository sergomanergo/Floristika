package ru.kazachkov.florist.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import lombok.Getter;
import ru.kazachkov.florist.interfaces.SpinnerText;

/**
 * Created by ishmukhametov on 21.10.16.
 */

public class FloristSpinnerAdapter<T extends SpinnerText> extends android.widget.BaseAdapter {

    @Getter
    private List<T> items;

    public FloristSpinnerAdapter(List<T> authors) {
        setList(items);
    }

    public void replaceData(List<T> items) {
        setList(items);
    }

    private void setList(List<T> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public T getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        T item = getItem(i);
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            view = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, viewGroup, false);
        }
        ((TextView) view).setText(item.getSpinnerText());
        return view;
    }

}
