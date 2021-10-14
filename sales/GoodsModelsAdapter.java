package ru.kazachkov.florist.sales;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.kazachkov.florist.BR;
import ru.kazachkov.florist.R;
import ru.kazachkov.florist.composition.CompositionContract;
import ru.kazachkov.florist.model.GoodsModel;
import ru.kazachkov.florist.viewmodel.impl.GoodsVMImpl;

/**
 * Created by ishmukhametov on 27.10.16.
 */

public class GoodsModelsAdapter extends RecyclerView.Adapter<GoodsModelsAdapter.ViewHolder> {

    private final CompositionContract.Presenter presenter;
    private LayoutInflater layoutInflater;

    private List<GoodsModel> goodsModels;

    public GoodsModelsAdapter(List<GoodsModel> goodsModels, CompositionContract.Presenter presenter) {
        this.presenter = presenter;
        setList(goodsModels);
    }

    public void replaceData(List<GoodsModel> items) {
        setList(items);
    }

    private void setList(List<GoodsModel> items) {
        this.goodsModels = items;
        notifyDataSetChanged();
    }

    @Override
    public GoodsModelsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (layoutInflater == null) layoutInflater = LayoutInflater.from(viewGroup.getContext());
        return new GoodsModelsAdapter.ViewHolder(layoutInflater.inflate(R.layout.item_goods_lite, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(GoodsModelsAdapter.ViewHolder viewHolder, int i) {
        viewHolder.binding.setVariable(BR.vm, new GoodsVMImpl(goodsModels.get(i)));
        viewHolder.binding.setVariable(BR.presenter, presenter);
        viewHolder.binding.executePendingBindings();
    }


    @Override
    public int getItemCount() {
        return goodsModels.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ViewDataBinding binding;

        public ViewHolder(View rowView) {
            super(rowView);
            this.binding = DataBindingUtil.bind(rowView);
        }
    }
}
