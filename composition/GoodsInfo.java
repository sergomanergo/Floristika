package ru.kazachkov.florist.composition;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.Nullable;
import android.view.View;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ru.kazachkov.florist.BR;
import ru.kazachkov.florist.GalleryActivity;
import ru.kazachkov.florist.api.model.Photo;
import ru.kazachkov.florist.model.GoodsModel;
import ru.kazachkov.florist.model.SaleComposModel;
import rx.Observable;

/**
 * Created by ishmukhametov on 19.10.16.
 */

public class GoodsInfo extends BaseObservable {
    public final BigDecimal ZERO = new BigDecimal(0);

    private String goodsName;
    private BigDecimal initBalance;
    private BigDecimal balance;
    private BigDecimal initCount;
    private BigDecimal count;
    private String unitsName;
    private BigDecimal price;
    private BigDecimal total;
    private BigDecimal minUnit;
    private List<Photo> photos;
    private final boolean isInteger;

    @Getter
    private boolean isAdding = false;
    @Getter
    private SaleComposModel saleComposModel;

    private boolean isCountUpdating = false;

    @Getter
    private boolean isOverspendingOfGoodsEnabled = true;


    public GoodsInfo(SaleComposModel saleComposModel,GoodsModel goodsModel, @Nullable BigDecimal count, @Nullable BigDecimal price) {
        double stockItems = goodsModel.getStock().getStockItems();
        double edIzmMinVal = goodsModel.getEdIzm().getEdIzmMinVal();
        isInteger = edIzmMinVal == 1;
        initBalance = new BigDecimal(stockItems /*- (stockItems % edIzmMinVal)*/).setScale(2, BigDecimal.ROUND_HALF_UP);
        minUnit = new BigDecimal(edIzmMinVal).setScale(isInteger ? 0 : 2, RoundingMode.DOWN);
        goodsName = goodsModel.getName();
        unitsName = goodsModel.getEdIzm().getEdIzmName();
        photos = goodsModel.getPhoto();
        this.saleComposModel = saleComposModel;

        isAdding = price == null;
        if (isAdding) {
            this.price = new BigDecimal(goodsModel.getItemPrices().getRegularPrice());
        } else {
            this.price = price;
        }

        if (count == null) {
            this.initCount = new BigDecimal(goodsModel.getEdIzm().getEdIzmMinVal());
        } else {
            this.initCount = count;
        }

        setupBalanceAndCount();
    }

    public void showImage(View view) {
        Observable.from(photos)
                .map(Photo::getPath)
                .toList()
                .subscribe(strings -> {
                    GalleryActivity.start(view.getContext(), 0, new ArrayList<>(strings));
                });
    }

    private void setupBalanceAndCount(){
        initBalance = initBalance.setScale(getScaleMode(), RoundingMode.DOWN);
        initCount = initCount.setScale(getScaleMode(), RoundingMode.DOWN);

        balance = initBalance.subtract(initCount);
        count = initCount;

        if(!isOverspendingOfGoodsEnabled){
            if(balance.compareTo(ZERO) < 0){
                BigDecimal debt = ZERO.subtract(balance);
                balance = ZERO;
                if(count.subtract(debt).compareTo(ZERO) > 0){
                    count = count.subtract(debt);
                }else {
                    count = ZERO;
                }
            }
        }

        recountTotal();
    }


    /*-----------------math & logic-----------------*/

    public void addOne() {
        deltaCountAndNotify(minUnit, true);
    }

    public void subtractOne() {
        deltaCountAndNotify(minUnit, false);
    }

    private void updateCount(BigDecimal count) {
        if(count.compareTo(ZERO) >= 0) {
            BigDecimal diff = count.subtract(this.count);
            deltaCount(diff.abs(), diff.compareTo(ZERO) >= 0);
        }else updateCount(ZERO);
    }

    private void deltaCount(BigDecimal val, boolean isSumming){
        if(isSumming){
            if(isOverspendingOfGoodsEnabled || balance.compareTo(val) >= 0){
                count = count.add(val);
                balance = balance.subtract(val);
            }else {
                this.count = count.add(balance);
                balance = ZERO;
                //достигнут предел возможного количества
                notifyPropertyChanged(BR.count);
            }
        }else {
            if(count.compareTo(val) >= 0){
                count = count.subtract(val);
                balance = balance.add(val);
            }
        }

        recountTotal();
    }

    public void updatePrice(BigDecimal price) {
        this.price = price.setScale(0, RoundingMode.DOWN);
    }

    private void updateTotal(BigDecimal total) {
        this.total = total;

        if(isInteger & count.compareTo(ZERO) > 0){
            updatePrice(total.divide(count,getScaleMode(), RoundingMode.DOWN));
        }else if(price.compareTo(ZERO) != 0){
            updateCount(total.divide(price,getScaleMode(), RoundingMode.DOWN));
        }
    }

    private void recountTotal() {
        total = price.multiply(count).setScale(0, RoundingMode.UP);
    }

    private int getScaleMode(){ return 2;/*isInteger ? 0 : 2;*/ }

    /*-----------------notification-----------------*/

    private void deltaCountAndNotify(BigDecimal val, boolean isSumming){
        deltaCount(val, isSumming);
        notifyPropertyChanged(BR.balance);
        notifyPropertyChanged(BR.count);
        notifyPropertyChanged(BR.total);
    }

    private void updateCountAndNotify(BigDecimal val){
        updateCount(val);
        notifyPropertyChanged(BR.count);
        notifyPropertyChanged(BR.balance);
        notifyPropertyChanged(BR.total);
    }

    private void updatePriceAndNotify(BigDecimal val){
        updatePrice(val);
        recountTotal();
        notifyPropertyChanged(BR.price);
        notifyPropertyChanged(BR.total);
    }

    private void updateTotalAndNotify(BigDecimal val){
        updateTotal(val);
        notifyPropertyChanged(BR.balance);
        notifyPropertyChanged(BR.price);
        notifyPropertyChanged(BR.count);
    }


    /*-----------------setters-----------------*/

    public void setCount(String s) {
        try {
            BigDecimal newCount = new BigDecimal(s);
            if(newCount.compareTo(count) != 0) {
                updateCountAndNotify(newCount);
            }
        }catch (Exception e){
            updateCount(ZERO);
        }
    }

    public void setPrice(String s) {
        try {
            BigDecimal newPrice = new BigDecimal(s);
            if(newPrice.compareTo(price) != 0) {
                updatePriceAndNotify(newPrice);
            }
        }catch (Exception e){
            updatePrice(ZERO);
        }
    }


    public void setTotal(String s) {
        try {
            BigDecimal newTotal = new BigDecimal(s);
            if(newTotal.compareTo(total) != 0) {
                updateTotalAndNotify(newTotal);
            }
        }catch (Exception e){
            updateTotal(ZERO);
        }
    }

    public void setIsOverspendingOfGoodsEnabled(boolean val){
        isOverspendingOfGoodsEnabled = val;
        setupBalanceAndCount();
        notifyPropertyChanged(BR.count);
        notifyPropertyChanged(BR.balance);
        notifyPropertyChanged(BR.total);
    }


    /*-----------------getters-----------------*/

    @Bindable
    public String getGoodsName() {
        return goodsName;
    }

    @Bindable
    public String getCount() {
        if(count.compareTo(count.setScale(0, RoundingMode.DOWN)) == 0)
            return count.setScale(0, RoundingMode.DOWN).toPlainString();
        return count.setScale(1, RoundingMode.DOWN).toString();
    }

    public BigDecimal getCountNum(){
        return count;
    }

    @Bindable
    public String getBalance() {
        return balance.setScale(getScaleMode(), RoundingMode.DOWN).toString();
    }


    @Bindable
    public String getUnitsName() {
        return unitsName;
    }

    @Bindable
    public boolean getShowPhotosIcon() {
        return !(photos == null || photos.size() == 0);
    }


    @Bindable
    public String getPrice() {
        return price.toString();
    }

    public BigDecimal getPriceNum(){
        return price;
    }


    @Bindable
    public String getTotal() {
        return total.setScale(0, RoundingMode.UP).toString();
    }

}
