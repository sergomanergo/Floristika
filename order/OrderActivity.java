package ru.kazachkov.florist.order;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import ru.kazachkov.florist.BaseActivity;
import ru.kazachkov.florist.R;
import ru.kazachkov.florist.api.model.GoodDT;
import ru.kazachkov.florist.api.model.GoodSMDiscDT;
import ru.kazachkov.florist.api.model.Order2Seller;
import ru.kazachkov.florist.api.model.OrderDetailed;
import ru.kazachkov.florist.api.model.OrderDetailedData4;
import ru.kazachkov.florist.interfaces.OrderData;
import ru.kazachkov.florist.order.paymentdata.PaymentDataFragment;
import ru.kazachkov.florist.order.paymentdata.PaymentDataPresenter;
import ru.kazachkov.florist.order.paymentdata.PaymentDataViewModel;
import ru.kazachkov.florist.order.photos.OrderPhotoContract;
import ru.kazachkov.florist.order.photos.OrderPhotoFragment;
import ru.kazachkov.florist.order.photos.OrderPhotoPresenter;
import ru.kazachkov.florist.order.sellerdata.SellerDataFragment;
import ru.kazachkov.florist.order.sellerdata.SellerDataPresenter;
import ru.kazachkov.florist.order.sellerdata.SellerDataViewModel;
import ru.kazachkov.florist.tools.Const;
import ru.kazachkov.florist.tools.MainSchedulers;
import ru.kazachkov.florist.tools.PhotoHelper;
import ru.kazachkov.florist.tools.SaleMode;
import ru.kazachkov.florist.tools.Utils;

import static ru.kazachkov.florist.tools.Const.REQUEST_ORDER_SCREEN;


public class OrderActivity extends BaseActivity {

    private PaymentDataPresenter paymentDataPresenter;
    private SellerDataPresenter sellerDataPresenter;
    private OrderPhotoContract.Presenter orderPhotoPresenter;

    private SaleMode saleMode;
    private SellerDataViewModel sellerDataViewModel;
    private String basePrice;
    private BigDecimal totalPrice;
    private List<GoodDT> listGoods;
    private PaymentDataViewModel paymentDataViewModel;
    private OrderData orderData;
    private Integer isPayTypeCash;
    private double discount;


    public static void start(Context context) {
        Intent starter = new Intent(context, OrderActivity.class);
        context.startActivity(starter);
    }


    public static void start(Activity context, SaleMode saleMode, String basePrice,String totalPrice, List<GoodDT> listGoods, OrderData orderData, Integer isPayTypeCash, double discount) {
        Intent starter = new Intent(context, OrderActivity.class);
        starter.putExtra(Const.PRCL_SALE_MODE, saleMode);
        starter.putExtra(Const.PRCL_BASE_PRICE, basePrice);
        starter.putExtra(Const.PRCL_TOTAL_PRICE, totalPrice);
        starter.putExtra(Const.PAY_TYPE_CASH, isPayTypeCash);
        starter.putExtra(Const.PAY_DISCOUNT, discount);
        starter.putParcelableArrayListExtra(Const.PRCL_GOODS_LIST, (ArrayList<? extends Parcelable>) listGoods);
        if (orderData != null) {
            if (orderData instanceof OrderDetailedData4) {
                starter.putExtra(Const.EXTRA_ORDER_SAVED, (OrderDetailedData4) orderData);
            } else if (orderData instanceof OrderDetailed) {
                starter.putExtra(Const.EXTRA_ORDER_SAVED, (OrderDetailed) orderData);
            } else if (orderData instanceof Order2Seller) {
                starter.putExtra(Const.EXTRA_ORDER_SAVED, (Order2Seller) orderData);
            } else {
                throw new IllegalArgumentException("only OrderDetailedData | OrderDetailed | Order2Seller");
            }
        }
        context.startActivityForResult(starter, REQUEST_ORDER_SCREEN);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        basePrice = getIntent().getStringExtra(Const.PRCL_BASE_PRICE);
        totalPrice = new BigDecimal(getIntent().getStringExtra(Const.PRCL_TOTAL_PRICE));

        orderData = getIntent().getParcelableExtra(Const.EXTRA_ORDER_SAVED);

        isPayTypeCash = getIntent().getExtras().getInt(Const.PAY_TYPE_CASH);

        discount = getIntent().getExtras().getDouble(Const.PAY_DISCOUNT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayShowTitleEnabled(false);
        }

        saleMode = (SaleMode) getIntent().getExtras().getSerializable(Const.PRCL_SALE_MODE);
        listGoods = getIntent().getParcelableArrayListExtra(Const.PRCL_GOODS_LIST);


        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);

        if (viewPager != null) {
            viewPager.setAdapter(new OrderDataFragmentAdapter(getSupportFragmentManager()));
        }

        AppCompatSpinner spinner = (AppCompatSpinner) findViewById(R.id.spinner);

        if (spinner != null) {
            EnumSet<SaleMode> saleModeEnumSet = EnumSet.allOf(SaleMode.class);
            List<SaleMode> saleModes = new ArrayList<>(saleModeEnumSet);

            ArrayAdapter<SaleMode> adapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item, saleModes);
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setSelection(saleModes.indexOf(saleMode));
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    SaleMode saleMode = SaleMode.values()[i];

                    if (sellerDataViewModel != null) {
                        sellerDataViewModel.setSaleMode(saleMode);
                    }

                    if (paymentDataPresenter != null) {
                        paymentDataViewModel.setSaleMode(saleMode);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }

    public OrderActivity addTotalPrice(BigDecimal totalPrice){
        this.totalPrice = totalPrice;
        return this;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(Const.PRCL_GOODS_LIST, (ArrayList<? extends Parcelable>) listGoods);
        intent.putExtra(Const.PAY_DISCOUNT, paymentDataPresenter.getDiscount().doubleValue());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private class OrderDataFragmentAdapter extends FragmentStatePagerAdapter {

        OrderDataFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                default:
                case 0:
                    SellerDataFragment sellerDataFragment = SellerDataFragment.newInstance();


                    sellerDataPresenter = new SellerDataPresenter(OrderActivity.this, controller, sellerDataFragment, preferences, new MainSchedulers());
                    sellerDataPresenter.setBasePrice(basePrice);
                    sellerDataPresenter.init(orderData);

                    sellerDataViewModel = new SellerDataViewModel(sellerDataPresenter);
                    sellerDataFragment.setViewModel(sellerDataViewModel);
                    sellerDataViewModel.setSaleMode(saleMode);
                    return sellerDataFragment;
                case 1:
                    PaymentDataFragment paymentDataFragment = PaymentDataFragment.newInstance();
                    paymentDataPresenter = new PaymentDataPresenter(controller, preferences, paymentDataFragment, saleMode, isPayTypeCash);

                    paymentDataPresenter.init(basePrice,totalPrice.toString(), listGoods, orderData, discount);
                    //paymentDataPresenter.setPrice(totalPrice);
                    paymentDataViewModel = new PaymentDataViewModel(paymentDataPresenter);
                    paymentDataFragment.setViewModel(paymentDataViewModel);
                    paymentDataFragment.setAppPreferences(preferences);
                    paymentDataViewModel.setSaleMode(saleMode);
                    //paymentDataViewModel.onChangeBasePrice(value -> sellerDataViewModel.setBasePrice(value));

                    return paymentDataFragment;
                case 2:
                    OrderPhotoFragment orderPhotoFragment = OrderPhotoFragment.newInstance();
                    orderPhotoPresenter = new OrderPhotoPresenter(orderPhotoFragment, controller, orderData);
                    orderPhotoFragment.setPresenter(orderPhotoPresenter);
                    return orderPhotoFragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public float getPageWidth(int position) {
            switch (position) {
                default:
                case 0:
                    return 0.31f;
                case 1:
                    return 0.63f;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.order_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_photo:
                takePhoto();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Const.REQUEST_CODE_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    orderPhotoPresenter.onPhotoSuccessfulTake();
                } else {
                    Utils.toast(this, R.string.error_cannot_create_image);
                }
                break;
        }
    }

    private void takePhoto() {
        File photoFile = PhotoHelper.createImageFile(this);
        if (photoFile == null) {
            return;
        }
        orderPhotoPresenter.handlePhotoPath(photoFile.getPath());
        PhotoHelper.dispatchTakePictureIntent(this, photoFile);
    }

    public void sendOrder(View view) {
        paymentDataPresenter.prepareAndSendOrder(sellerDataPresenter.getAuthorId(),
                sellerDataPresenter.getClient1cId(),
                orderPhotoPresenter.getNewPhotos(),
                saleMode);
    }
}
