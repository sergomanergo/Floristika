package ru.kazachkov.florist.composition;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import java.math.BigDecimal;
import java.time.Duration;

import ru.kazachkov.florist.R;
import ru.kazachkov.florist.databinding.LayoutCountDialogBinding;
import ru.kazachkov.florist.viewmodel.impl.ComponentVMImpl;


public class GoodsInfoDialog extends AppCompatDialogFragment {

    private GoodsInfo viewModel;
    private GoodsInfoDialogResultListener goodsInfoDialogResultListener;
    private ComponentVMImpl componentVM;
    private LayoutCountDialogBinding binding;

    public GoodsInfoDialog() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(AppCompatDialogFragment.STYLE_NO_TITLE, R.style.Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.layout_count_dialog, container, false);
        binding.setViewModel(viewModel);

        Button okButton = binding.buttons.okButton;
        Button cancelButton = binding.buttons.cancelButton;

        cancelButton.setOnClickListener(view -> {
            onCloseDialog();
        });

        okButton.setOnClickListener(view -> {
            if (goodsInfoDialogResultListener != null) {
                    if(viewModel.getCountNum().compareTo(BigDecimal.ZERO) > 0
                            && viewModel.getPriceNum().compareTo(BigDecimal.ZERO) > 0
                            && (new BigDecimal(viewModel.getTotal())).compareTo(BigDecimal.ZERO) > 0
                            ){
                        goodsInfoDialogResultListener.onResult(viewModel.getCount(), viewModel.getPrice());
                        if(viewModel.isAdding()){
                            viewModel.getSaleComposModel().add(componentVM, true);
                        }else {
                            viewModel.getSaleComposModel().updateSum();
                        }

                        onCloseDialog();
                }else {
                        Toast.makeText(getActivity(), "Знасения в полях ввода должны быть больше нуля!", Toast.LENGTH_LONG).show();
                    }
            }
        });



        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.countEditText.selectAll();
    }

    public void setViewModel(GoodsInfo viewModel) {
        this.viewModel = viewModel;
    }

    public void setGoodsInfoDialogResultListener(GoodsInfoDialogResultListener goodsInfoDialogResultListener) {
        this.goodsInfoDialogResultListener = goodsInfoDialogResultListener;
    }

    public void setComponentVM(ComponentVMImpl componentVM) {
        this.componentVM = componentVM;
        setGoodsInfoDialogResultListener(componentVM);
    }

    public interface GoodsInfoDialogResultListener {
        void onResult(String count, String price);
    }

    private void onCloseDialog(){
        hideKeyboard(getActivity());
        dismiss();
    }

    public static boolean hideKeyboard(Activity activity) {
        boolean isFocused = true;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            isFocused = false;
            view = new View(activity);
        }else {
            view.clearFocus();
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        return isFocused;
    }

}
