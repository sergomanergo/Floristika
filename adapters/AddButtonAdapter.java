package ru.kazachkov.florist.adapters;

import android.databinding.ObservableArrayList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import ru.kazachkov.florist.viewmodel.vm.AddVM;


/**
 * Created by ishmukhametov on 04.11.15.
 */

public class AddButtonAdapter<VM> extends BaseAdapter<VM> {

    private final AddVM addVM;
    private static final int ITEM_TYPE = 0;
    private static final int ADD_TYPE = 1;

    public AddButtonAdapter(int mLayoutId, int itemId, ObservableArrayList<VM> itemsVM, AddVM addVM) {
        super(mLayoutId, itemId, itemsVM);
        this.addVM = addVM;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        if (mLayoutInflater == null) mLayoutInflater = LayoutInflater.from(viewGroup.getContext());
        if (type == ADD_TYPE) {
            return new ViewHolder(mLayoutInflater.inflate(addVM.getAddLayoutId(), viewGroup, false));
        }
        return super.onCreateViewHolder(viewGroup, type);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        if (viewHolder.getItemViewType() == ITEM_TYPE) {
            super.onBindViewHolder(viewHolder, i);
        } else {
            viewHolder.binding.setVariable(addVM.getAddItemId(), addVM);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == getItemCount() - 1 ? ADD_TYPE : ITEM_TYPE;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }
}
