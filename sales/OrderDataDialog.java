package ru.kazachkov.florist.sales;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ru.kazachkov.florist.R;
import ru.kazachkov.florist.databinding.LayoutOrderDataBinding;
import ru.kazachkov.florist.model.md.SaleModel;


public class OrderDataDialog extends AppCompatDialogFragment {

    private OrderDataDialogInfo orderDataDialogInfo;
    private OnResultListener onResultListener;
    private SaleModel saleModel;

    public OrderDataDialog() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(AppCompatDialogFragment.STYLE_NO_TITLE, R.style.Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LayoutOrderDataBinding binding = DataBindingUtil.inflate(inflater, R.layout.layout_order_data, container, false);
        OrderDataViewModel viewModel = new OrderDataViewModel(orderDataDialogInfo);
        binding.setViewModel(viewModel);

        Button okButton = binding.buttons.okButton;
        Button cancelButton = binding.buttons.cancelButton;

        cancelButton.setOnClickListener(view -> {
            dismiss();
        });

        okButton.setOnClickListener(view -> {
            if (viewModel.commentChanged()) {
                onResultListener.commentResult(viewModel.getComment(), saleModel);
            }
            dismiss();
        });

        return binding.getRoot();
    }

    public void setOrderDataDialogInfo(OrderDataDialogInfo orderDataDialogInfo) {
        this.orderDataDialogInfo = orderDataDialogInfo;
    }

    public void setOnResultListener(OnResultListener onResultListener) {
        this.onResultListener = null;
        this.onResultListener = onResultListener;
    }

    public void setData(OrderDataDialogInfo orderDataDialogInfo, SaleModel saleModel) {
        setOrderDataDialogInfo(orderDataDialogInfo);
        this.saleModel = saleModel;
    }

    public interface OnResultListener {
        void commentResult(String comment, SaleModel saleModel);
    }
}
