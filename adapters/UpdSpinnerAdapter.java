package ru.kazachkov.florist.adapters;


import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.kazachkov.florist.interfaces.SpinnerText;

public class UpdSpinnerAdapter extends android.widget.BaseAdapter {

    Context context;
    int layoutId;
    ObservableArrayList<? extends SpinnerText> items;
    private final LayoutInflater inflater;

    ObservableList.OnListChangedCallback<ObservableList<?>> onListChangedCallback =
            new ObservableList.OnListChangedCallback<ObservableList<?>>() {
                @Override
                public void onChanged(ObservableList<?> vms) {
                    notifyDataSetChanged();
                }

                @Override
                public void onItemRangeChanged(ObservableList<?> vms, int i, int i1) {
                    notifyDataSetChanged();
                }

                @Override
                public void onItemRangeInserted(ObservableList<?> vms, int i, int i1) {
                    notifyDataSetChanged();
                }

                @Override
                public void onItemRangeMoved(ObservableList<?> vms, int i, int i1, int i2) {
                    notifyDataSetChanged();
                }

                @Override
                public void onItemRangeRemoved(ObservableList<?> vms, int i, int i1) {
                    notifyDataSetChanged();
                }
            };

    public UpdSpinnerAdapter(Context context, int layoutId, ObservableArrayList<? extends SpinnerText> items) {
        this.context = context;
        this.layoutId = layoutId;
        this.items = items;
        inflater = LayoutInflater.from(context);
        items.addOnListChangedCallback(onListChangedCallback);
    }


    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(layoutId, viewGroup, false);
        }
        ((TextView) view).setText(items.get(i).getSpinnerText());
        return view;
    }
}
