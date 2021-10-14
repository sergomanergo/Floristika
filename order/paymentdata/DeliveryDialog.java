package ru.kazachkov.florist.order.paymentdata;

import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.math.BigDecimal;

import ru.kazachkov.florist.R;
import ru.kazachkov.florist.databinding.LayoutDeliveryDialogBinding;
import ru.kazachkov.florist.order.sellerdata.Delivery;


public class DeliveryDialog extends AppCompatDialogFragment {

    private static final String RECEIVED1 = "received";
    private static final String PAYMENT = "payment";
    private static final String RETURNPAY = "returnpay";
    private String payment;
    private String received;
    private boolean returnPay;

    private OnResultListener onResultListener;

    public static DeliveryDialog newInstance(String received, String payment, Boolean returnPay) {

        Bundle args = new Bundle();

        DeliveryDialog fragment = new DeliveryDialog();
        args.putString(RECEIVED1, received);
        args.putString(PAYMENT, payment);
        args.putBoolean(RETURNPAY, returnPay);
        fragment.setArguments(args);
        return fragment;
    }


    public DeliveryDialog() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(AppCompatDialogFragment.STYLE_NO_TITLE, R.style.Dialog);
        payment = getArguments().getString(PAYMENT, "0");
        received = getArguments().getString(RECEIVED1, "0");
        returnPay = getArguments().getBoolean(RETURNPAY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        LayoutDeliveryDialogBinding binding = DataBindingUtil.inflate(inflater,R.layout.layout_delivery_dialog, container, false);
        Delivery delivery = new Delivery(getResources());
        delivery.setAdditionalExpenses("0");
        delivery.setDelivery2("0");
        delivery.setCard(true);
        delivery.setPayment(this.payment);
        delivery.setPayment2(new BigDecimal(this.payment));
        delivery.setReceived(received);
        delivery.setShowAdditionalConsumption(false);
        delivery.setReturnPay(returnPay);

        if(returnPay) {
            delivery.setPayment(String.valueOf( Math.abs( Double.parseDouble(payment)) ) );
            delivery.setPayment2( new BigDecimal( Math.abs( Double.parseDouble(payment) ))  );
            binding.signText.setText("+");
            binding.receivedText.setText("Получено");
            binding.paymentText.setText("Возврат");
            binding.deliveryText.setText("Сдача");
        }

        Button okButton = binding.buttons.okButton;
        Button cancelButton = binding.buttons.cancelButton;

        cancelButton.setOnClickListener(view -> {
            dismiss();
        });

        okButton.setOnClickListener(view -> {
            dismiss();
            if (onResultListener != null) {
                onResultListener.deliveryResult(delivery.getReceived().replaceAll("-", ""), delivery.getPayment().replaceAll("-", ""), delivery.getDelivery());
            }
        });

        binding.setViewModel(delivery);
        return binding.getRoot();
    }

    public void setOnResultListener(OnResultListener onResultListener) {
        this.onResultListener = null;
        this.onResultListener = onResultListener;
    }

    public interface OnResultListener {

        void deliveryResult(String received, String payment, String delivery);

    }

}
