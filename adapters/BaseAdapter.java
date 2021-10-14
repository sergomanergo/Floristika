package ru.kazachkov.florist.adapters;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

import ru.kazachkov.florist.tools.WeakOnListChangedCallback;

/**
 * Created by ishmukhametov on 23.10.15.
 * The class adapting view for all list in the app
 */

public class BaseAdapter<VM> extends RecyclerView.Adapter<BaseAdapter.ViewHolder> {


    private final int layoutId;
    private final Map<Integer, Object> objectMap;

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
                    notifyItemRangeRemoved(i, i1);
                }
            };
    private ObservableArrayList<?> itemsVM;
    private int itemId;

    public BaseAdapter(int layoutId, int itemId, ObservableArrayList<?> itemsVM) {
        this.itemId = itemId;
        this.layoutId = layoutId;
        this.itemsVM = itemsVM;
        this.objectMap = null;
        itemsVM.addOnListChangedCallback(new WeakOnListChangedCallback<>(onListChangedCallback));
    }

    public BaseAdapter(int layoutId, int itemId, ObservableArrayList<?> itemsVM, Map<Integer, Object> objectMap) {
        this.layoutId = layoutId;
        this.itemId = itemId;
        this.itemsVM = itemsVM;
        this.objectMap = objectMap;
        itemsVM.addOnListChangedCallback(new WeakOnListChangedCallback<>(onListChangedCallback));
    }

    protected LayoutInflater mLayoutInflater;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (mLayoutInflater == null) mLayoutInflater = LayoutInflater.from(viewGroup.getContext());
        return new ViewHolder(mLayoutInflater.inflate(layoutId, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.binding.setVariable(itemId, itemsVM.get(i));
        if (objectMap != null) {
            for (Integer varId : objectMap.keySet()) {
                viewHolder.binding.setVariable(varId, objectMap.get(varId));
            }
        }
        viewHolder.binding.executePendingBindings();
    }

    public  void setOnElementSwipeBinding(ViewDataBinding binding){
        ObservableList.OnListChangedCallback<ObservableList<?>> callback =
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
                        notifyItemRangeRemoved(i, i1);
                    }
                };
    }


    @Override
    public int getItemCount() {
        return itemsVM.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ViewDataBinding binding;

        public ViewHolder(View rowView) {
            super(rowView);
            this.binding = DataBindingUtil.bind(rowView);
        }
    }

}
