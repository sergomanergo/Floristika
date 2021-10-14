package ru.kazachkov.florist.adapters;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ru.kazachkov.florist.BR;
import ru.kazachkov.florist.R;
import ru.kazachkov.florist.sales.SalesContract;
import ru.kazachkov.florist.sales.SalesItemActionHandler;
import ru.kazachkov.florist.tools.Utils;
import ru.kazachkov.florist.viewmodel.vm.SaleVM;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;


public class SalesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private final SalesContract.View view;

    private List<Pair<Integer, Integer>> typeMap;
    private final SalesItemActionHandler salesItemActionHandler;

    private final List<DateTime> headers;
    private final List<SaleVM> items;
    private Boolean isHeadersSkip;

    public SalesListAdapter(SalesItemActionHandler salesItemActionHandler, SalesContract.View view, Boolean isHeadersSkip) {
        this.salesItemActionHandler = salesItemActionHandler;
        this.view = view;
        headers = new ArrayList<>();
        items = new ArrayList<>();
        typeMap = new ArrayList<>();
        this.isHeadersSkip = isHeadersSkip;
    }

    public void clear() {
        typeMap.clear();
        headers.clear();
        items.clear();
        view.showCurrentDateInHeader("");
        notifyDataSetChanged();
    }

    public void showItems(List<SaleVM> collectionMap,Boolean isHeadersSkip) {
        this.isHeadersSkip = isHeadersSkip;
        prepareData(collectionMap);
        if (!isHeadersSkip) {
            prepareData(collectionMap);
            if (!headers.isEmpty()) {
                view.showCurrentDateInHeader(Utils.textDateOf(headers.get(0), "dd MMMM"));
            }
        }
        notifyDataSetChanged();
    }

    private void prepareData(List<SaleVM> collectionMap) {
        typeMap.clear();
        headers.clear();
        items.clear();

        int headerIndex = 0;
        int itemIndex = 0;
        String date = "";
        for (SaleVM entry : collectionMap) {

            items.add(entry);
            if (!isHeadersSkip)
            if(!entry.date().equals(date)) {
                date = entry.date();
                headers.add(entry.dateTime());
                typeMap.add(Pair.create(TYPE_HEADER, headerIndex++));
            }

            typeMap.add(Pair.create(TYPE_ITEM, itemIndex++));
        }
    }

    private LayoutInflater mLayoutInflater;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        if (type == TYPE_HEADER) {
            if (mLayoutInflater == null)
                mLayoutInflater = LayoutInflater.from(viewGroup.getContext());
            return new HeaderViewHolder(mLayoutInflater.inflate(R.layout.item_date, viewGroup, false));
        } else {
            if (mLayoutInflater == null)
                mLayoutInflater = LayoutInflater.from(viewGroup.getContext());
            return new ItemViewHolder(mLayoutInflater.inflate(R.layout.item_sale_v2, viewGroup, false));
        }

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).binding.setVariable(BR.saleVM, items.get(typeMap.get(i).second));
            ((ItemViewHolder) holder).binding.setVariable(BR.actionHandler, salesItemActionHandler);
            ((ItemViewHolder) holder).binding.executePendingBindings();
        } else {
            Integer second = typeMap.get(i).second;
            if (!headers.isEmpty()) {
                ((HeaderViewHolder) holder).date.setText(Utils.textDateOf(headers.get(second), "dd MMMM"));
            }
        }
    }


    public SaleVM getItemByPosition(int position) {
        return items.get(typeMap.get(position).second);
    }

    @Override
    public int getItemCount() {
        return Math.max(headers.size() - 1, 0) + items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    public boolean isPositionHeader(int position) {
        return typeMap.get(position).first == TYPE_HEADER;
    }

    public void updateItem(String comment, String id) {
        Observable.from(items).first(saleVM -> saleVM.getId().equals(id))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(saleVM -> {
                    saleVM.setComment(comment);
                    notifyDataSetChanged();
                });
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        public final ViewDataBinding binding;

        ItemViewHolder(View rowView) {
            super(rowView);
            this.binding = DataBindingUtil.bind(rowView);
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public final TextView date;

        HeaderViewHolder(View rowView) {
            super(rowView);
            date = (TextView) rowView;
        }
    }


}
