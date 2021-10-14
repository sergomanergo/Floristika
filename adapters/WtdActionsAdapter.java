package ru.kazachkov.florist.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.kazachkov.florist.R;
import ru.kazachkov.florist.api.model.FastAction;
import ru.kazachkov.florist.sales.EFastAction;

public class WtdActionsAdapter extends android.widget.BaseAdapter {

    Context context;
    private final LayoutInflater inflater;
    private List<FastAction> items;


    public WtdActionsAdapter(Context context, List<FastAction> items) {
        this.context = context;
        this.items = items;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return items == null ? 0 : items.size();
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
            view = inflater.inflate(R.layout.item_wtd_action, viewGroup, false);
        }
        FastAction fastAction = items.get(i);
        ((TextView) view).setText(fastAction.toString());
        ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(EFastAction.getById(fastAction.getId()).getIconResourceId(), 0, 0, 0);
        return view;
    }
}
