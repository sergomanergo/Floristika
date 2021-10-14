package ru.kazachkov.florist.order.sellerdata;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ru.kazachkov.florist.R;
import ru.kazachkov.florist.data.DataController;
import ru.kazachkov.florist.databinding.LayoutAddClientBinding;
import ru.kazachkov.florist.model.Client;
import ru.kazachkov.florist.order.paymentdata.DeliveryDialog;


public class AddClientDialog extends AppCompatDialogFragment {

    private AddClientDialog.OnResultListener onResultListener;

    public AddClientDialog() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(AppCompatDialogFragment.STYLE_NO_TITLE, R.style.Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LayoutAddClientBinding binding = DataBindingUtil.inflate(inflater, R.layout.layout_add_client, container, false);
        Client client = new Client();
        client.setCardNumber("");
        client.setName("");
        client.setEmail("");
        client.setPhone("");

        binding.setClient(client);


        Button okButton = binding.buttons.okButton;
        Button cancelButton = binding.buttons.cancelButton;

        cancelButton.setOnClickListener(view -> {
            dismiss();
        });

        okButton.setOnClickListener(view -> {
            dismiss();
            if (onResultListener != null) {
                onResultListener.clientResult(client);
            }
        });

        return binding.getRoot();
    }

    public void setOnResultListener(AddClientDialog.OnResultListener onResultListener) {
        this.onResultListener = null;
        this.onResultListener = onResultListener;
    }

    public interface OnResultListener {

        void clientResult(Client client);

    }
}
