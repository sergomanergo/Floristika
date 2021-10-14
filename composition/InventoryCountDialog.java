package ru.kazachkov.florist.composition;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.math.BigDecimal;
import java.util.List;

import ru.kazachkov.florist.R;
import ru.kazachkov.florist.databinding.LayoutInventoryCountDialogBinding;


public class InventoryCountDialog extends AppCompatDialogFragment {

    private OnResultListener onResultListener;
    private InventoryCount viewModel;


    public InventoryCountDialog() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(AppCompatDialogFragment.STYLE_NO_TITLE, R.style.Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LayoutInventoryCountDialogBinding binding = DataBindingUtil.inflate(inflater, R.layout.layout_inventory_count_dialog, container, false);

        binding.setViewModel(viewModel);

        Button okButton = binding.buttons.okButton;
        Button cancelButton = binding.buttons.cancelButton;

        cancelButton.setOnClickListener(view -> {
            dismiss();
        });

        okButton.setOnClickListener(view -> {
            dismiss();
            if (onResultListener != null) {
                onResultListener.onInventoryTermsChanged(viewModel.getTerms(), new BigDecimal(viewModel.getCount()));
            }
        });

        return binding.getRoot();
    }

    public void setViewModel(InventoryCount inventoryCount) {
        this.viewModel = inventoryCount;
    }

    public void setOnResultListener(OnResultListener onResultListener) {
        this.onResultListener = null;
        this.onResultListener = onResultListener;
    }

    public interface OnResultListener {
        void onInventoryTermsChanged(List<BigDecimal> terms, BigDecimal count);
    }

}
