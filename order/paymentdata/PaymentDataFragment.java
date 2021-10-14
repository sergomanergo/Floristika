package ru.kazachkov.florist.order.paymentdata;


import android.app.Activity;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import ru.kazachkov.florist.BR;
import ru.kazachkov.florist.MainActivity;
import ru.kazachkov.florist.R;
import ru.kazachkov.florist.app.AppPreferences;
import ru.kazachkov.florist.databinding.FragmentPaymentDataBinding;
import ru.kazachkov.florist.logic.Error;
import ru.kazachkov.florist.tools.SimpleArrayAdapter;
import ru.kazachkov.florist.tools.Utils;
import ru.kazachkov.florist.viewmodel.PlaceSettingsModel;

import static ru.kazachkov.florist.tools.Preconditions.checkNotNull;

public class PaymentDataFragment extends Fragment implements PaymentDataContract.View {


    private AppPreferences appPreferences;
    private boolean skipChange = false;
    private PaymentDataContract.Presenter presenter;
    private PaymentDataViewModel viewModel;
    private ProgressDialog progressDialog;
    FragmentPaymentDataBinding dataBinding;
    @Nullable
    private SharedPreferences sharedPref;

    public static PaymentDataFragment newInstance() {

        Bundle args = new Bundle();

        PaymentDataFragment fragment = new PaymentDataFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private AlertDialog.Builder getInputDiaolg(){
        View customView = this.getLayoutInflater().inflate(R.layout.layout_mask, null);
        EditText inputField = customView.findViewById(R.id.valueInput);
        inputField.setText( presenter.getPrice().setScale(0, RoundingMode.UP).toString());
        return new AlertDialog.Builder(getActivity())
                .setTitle("Цена заказа")
                .setView(customView)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    try {
                        BigDecimal newPrice = new BigDecimal(inputField.getText().toString());

                        if(newPrice.compareTo(BigDecimal.ZERO) > 0){
                            ((PaymentDataPresenter)presenter).setPrice(newPrice);
                            viewModel.notifyPriceChanged();
                            viewModel.notifyPropertyChanged(BR.cash);
                            viewModel.notifyPropertyChanged(BR.cashless);
                            viewModel.notifyPropertyChanged(BR.balance);
                            viewModel.toDoList.notifyChange();
                            hideKeyboard(getActivity());
                            //model.setNewTotalPrice(newPrice.doubleValue());
                        }
                    }catch (Exception e){
                        //
                    }
                });
    }

    private AlertDialog.Builder getInputCashDiaolg(){
        View customView = this.getLayoutInflater().inflate(R.layout.layout_mask, null);
        EditText inputField = customView.findViewById(R.id.valueInput);
        inputField.setText( presenter.getCash().setScale(0, RoundingMode.UP).toString());

        CheckBox checkBox = customView.findViewById(R.id.valueCheckbox);
        if(presenter.getPaidSumCashTotal().floatValue() > 0) {
            inputField.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
            checkBox.setVisibility(View.VISIBLE);
        }

        inputField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(skipChange) return;
                skipChange = true;
                try {
                    if(inputField.getText().equals("")) return;

                    BigDecimal bigDecimal = new BigDecimal(0);
                    try {
                        bigDecimal =  new BigDecimal(inputField.getText().toString());
                    } catch (Exception ignored){};

                    // Unregister self before update
                    inputField.removeTextChangedListener(this);

                    // The trick to update text smoothly.
                    if (checkBox.isChecked()) {
                        s.replace(0, s.length(), bigDecimal.abs().setScale(0, RoundingMode.UP).negate().toString());
                    } else {
                        s.replace(0, s.length(), bigDecimal.abs().setScale(0, RoundingMode.UP).toString());
                    }

                    // Re-register self after update
                    inputField.addTextChangedListener(this);


                }finally {
                    skipChange = false;
                }
            }
        });

        checkBox.setText("Возврат");
        if( presenter.getCash().floatValue() < 0) {
            checkBox.setChecked(true);
        }

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputField.getText().equals("")) return;

                BigDecimal bigDecimal = new BigDecimal(0);
                try {
                    bigDecimal =  new BigDecimal(inputField.getText().toString());
                    if(bigDecimal.compareTo(presenter.getPaidSumCashTotal()) > 0) {
                        bigDecimal = presenter.getPaidSumCashTotal();
                    }
                } catch (Exception ignored){};

                if(!checkBox.isChecked()) {
                    inputField.setText(bigDecimal.setScale(0, RoundingMode.UP).negate().toString());
                } else
                {
                    inputField.setText(bigDecimal.setScale(0, RoundingMode.UP).toString());
                }
                inputField.setSelection(inputField.getText().length());
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setTitle("Оплачено наличными")
                .setView(customView)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    try {
                        BigDecimal newPrice = new BigDecimal(inputField.getText().toString());

                        ((PaymentDataPresenter)presenter).setCash(newPrice.toString());
                        viewModel.notifyPriceChanged();
                        viewModel.notifyPropertyChanged(BR.cash);
                        viewModel.notifyPropertyChanged(BR.cashless);
                        viewModel.notifyPropertyChanged(BR.balance);
                        viewModel.toDoList.notifyChange();
                        hideKeyboard(getActivity());

                    }catch (Exception e){
                        //
                    }
                });
    }


    private AlertDialog.Builder getInputCashLessDiaolg(){
        View customView = this.getLayoutInflater().inflate(R.layout.layout_mask, null);
        EditText inputField = customView.findViewById(R.id.valueInput);
        inputField.setText( presenter.getCashless().setScale(0, RoundingMode.UP).toString());

        CheckBox checkBox = customView.findViewById(R.id.valueCheckbox);
        if(presenter.getPaidSumBankTotal().floatValue() > 0) {
            inputField.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
            checkBox.setVisibility(View.VISIBLE);
        }

        inputField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(skipChange) return;
                skipChange = true;
                try {
                    if(inputField.getText().equals("")) return;

                    BigDecimal bigDecimal = new BigDecimal(0);
                    try {
                        bigDecimal =  new BigDecimal(inputField.getText().toString());
                    } catch (Exception ignored){};

                    // Unregister self before update
                    inputField.removeTextChangedListener(this);

                    // The trick to update text smoothly.
                    if (checkBox.isChecked()) {
                        s.replace(0, s.length(), bigDecimal.abs().setScale(0, RoundingMode.UP).negate().toString());
                    } else {
                        s.replace(0, s.length(), bigDecimal.abs().setScale(0, RoundingMode.UP).toString());
                    }

                    // Re-register self after update
                    inputField.addTextChangedListener(this);


                }finally {
                    skipChange = false;
                }
            }
        });

        checkBox.setText("Возврат");
        if( presenter.getCashless().floatValue() < 0) {
            checkBox.setChecked(true);
        }

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputField.getText().equals("")) return;

                BigDecimal bigDecimal = new BigDecimal(0);
                try {
                    bigDecimal = new BigDecimal(inputField.getText().toString());
                    if(bigDecimal.compareTo(presenter.getPaidSumBankTotal()) > 0) {
                        bigDecimal = presenter.getPaidSumBankTotal();
                    }
                } catch (Exception ignored){};

                if(!checkBox.isChecked()) {
                    inputField.setText(bigDecimal.setScale(0, RoundingMode.UP).negate().toString());
                } else
                {
                    inputField.setText(bigDecimal.setScale(0, RoundingMode.UP).toString());
                }
                inputField.setSelection(inputField.getText().length());
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setTitle("Оплачено безнал")
                .setView(customView)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    try {
                        BigDecimal newPrice = new BigDecimal(inputField.getText().toString());

                        ((PaymentDataPresenter)presenter).setCashless(newPrice.toString());
                        viewModel.notifyPriceChanged();
                        viewModel.notifyPropertyChanged(BR.cash);
                        viewModel.notifyPropertyChanged(BR.cashless);
                        viewModel.notifyPropertyChanged(BR.balance);
                        viewModel.toDoList.notifyChange();
                        hideKeyboard(getActivity());
                        //model.setNewTotalPrice(newPrice.doubleValue());

                    }catch (Exception e){
                        //
                    }
                });
    }

    private AlertDialog.Builder getInputCommentDiaolg(){
        View customView = this.getLayoutInflater().inflate(R.layout.layout_mask, null);
        EditText inputField = customView.findViewById(R.id.valueInput);
        inputField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        inputField.setPadding(40,40,40,40);
        inputField.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        inputField.setGravity(Gravity.LEFT);

        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        customView.setMinimumWidth((int) (displayRectangle.width()*0.8f));

        inputField.setText( ((PaymentDataPresenter)presenter).realComment ); //presenter.getComment() );
        return new AlertDialog.Builder(getActivity())
                .setTitle("Комментарий")
                .setView(customView)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    try {
                        String s = ((PaymentDataPresenter)presenter).origComment;
                        ((PaymentDataPresenter)presenter).setComment(s+ (s.equals("") ? "": "\n") +inputField.getText().toString());
                        ((PaymentDataPresenter)presenter).realComment = inputField.getText().toString();
                        viewModel.notifyPropertyChanged(BR.comment);
                        hideKeyboard(getActivity());
                        //model.setNewTotalPrice(newPrice.doubleValue());

                    }catch (Exception e){
                        //
                    }
                });
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment_data, container, false);
        dataBinding.setActionHandel(presenter);
        dataBinding.setViewModel(viewModel);
        dataBinding.setSalePntName(appPreferences.getSalePntName());


        this.sharedPref = this.getActivity().getSharedPreferences("X", Context.MODE_PRIVATE);
        if (sharedPref != null) {
            Gson gson = new Gson();
            PlaceSettingsModel placeSettings = gson.fromJson(sharedPref.getString(appPreferences.getSalePntName(), "{\"isAutoFloristCalc\":false,\"isCashPayAction\":true}"), PlaceSettingsModel.class);
            presenter.setIsPayCash(placeSettings.getCashPayAction());
        }

        Spinner discountSpinner = dataBinding.discountSpinner;

        ArrayAdapter<String> discountAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, presenter.getDiscountsList());
        discountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        discountSpinner.setAdapter(discountAdapter);

        discountSpinner.setSelection(presenter.getDiscountPosition());

        discountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println();
                presenter.setDiscountPosition(i);
                viewModel.notifyPriceChanged();
                //изменение скидки
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /*SimpleArrayAdapter adapter = ((SimpleArrayAdapter)((RecyclerView)dataBinding.getRoot().findViewById(R.id.recyclerPayData)).getAdapter());
        adapter.isGray = true;
        adapter.notifyDataSetChanged();*/

        return dataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindData();
    }

    private void bindData(){
        viewModel.liveBalance.observe(this, (Double d) -> {
            if(d < -2){
                ((TextView)getView().findViewById(R.id.tvOstatok)).setTextColor(getContext().getResources().getColor(R.color.price_neg));
            }else {
                ((TextView)getView().findViewById(R.id.tvOstatok)).setTextColor(getContext().getResources().getColor(R.color.grey_group));
            }
        });
        viewModel.livePayments.observe(this, (List<String> list) -> {
            RecyclerView view = getView().findViewById(R.id.recyclerPayData);
            if(view.getAdapter() == null){
                view.setAdapter(new SimpleArrayAdapter());
                view.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
            }
            SimpleArrayAdapter adapter = (SimpleArrayAdapter) view.getAdapter();
            adapter.isGray = true;
            ((SimpleArrayAdapter)view.getAdapter()).setItems(list);
        });

        dataBinding.priceField.setClickable(true);
        dataBinding.priceField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInputDiaolg().show();
            }
        });

        dataBinding.cashField.setClickable(true);
        dataBinding.cashField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInputCashDiaolg().show();
            }
        });

        dataBinding.cashlessField.setClickable(true);
        dataBinding.cashlessField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInputCashLessDiaolg().show();
            }
        });

        dataBinding.commentField.setClickable(true);
        dataBinding.commentField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInputCommentDiaolg().show();
            }
        });
    }

    @Override
    public void setPresenter(PaymentDataContract.Presenter presenter) {
        this.presenter = checkNotNull(presenter);
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.unsubscribe();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.subscribe();
    }

    public void setViewModel(PaymentDataViewModel viewModel) {
        this.viewModel = viewModel;
    }


    public void setAppPreferences(AppPreferences appPreferences) {
        this.appPreferences = appPreferences;
    }

    @Override
    public void showDeliveryDialog(String received, String payment) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        boolean returnPay = (presenter.getCash().floatValue() < 0);
        DeliveryDialog deliveryDialog = DeliveryDialog.newInstance(received, payment, returnPay);
        deliveryDialog.show(fm, "fragment_delivery");

        deliveryDialog.setOnResultListener(presenter);
    }

    @Override
    public void showDeliveryResult(boolean isShowDelivery, boolean hideButtonDelivery) {
        viewModel.setShowDelivery(isShowDelivery, hideButtonDelivery);
    }

    @Override
    public void showError(Throwable throwable) {
        Error.onError(getContext()).call(throwable);
        hideProgress();
    }

    @Override
    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.cancel();
        }
    }

    @Override
    public void showProgress() {
        progressDialog = null;
        progressDialog = ProgressDialog.show(getContext(), "Обработка", "Обработка заказа");
        progressDialog.setCancelable(false);
    }

    @Override
    public void showScreenAfterSendOrder() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        Utils.toast(getContext(), R.string.new_order_added);
    }

    @Override
    public void updatePayment(Boolean state) {
        viewModel.updatePayment();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
