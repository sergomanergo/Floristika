package ru.kazachkov.florist.model;

import android.support.v4.util.Pair;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Data;
import lombok.ToString;
import ru.kazachkov.florist.api.model.GoodDT;
import ru.kazachkov.florist.tools.SaleMode;
import rx.Observable;

@Data
@ToString(exclude = "goodsModel")
public class ComponentModel {
    private GoodsModel goodsModel;
    private double count;
    private double sum;
    private double price;
    private double purchasePrice;
    private double regularPrice;
    private double edIzmMinValue;
    private double countInStock;
    private boolean purchase;
    private String edIzmName;
    private char[] balanceCount;
    private List<BigDecimal> terms = new ArrayList<>();


    private BigDecimal startBalance;


    public ComponentModel(GoodsModel goodsModel) {
        init(goodsModel, goodsModel.getItemPrices().getRegularPrice(), goodsModel.getEdIzm().getEdIzmMinVal());
    }

    public ComponentModel(GoodsModel goodsModel, double price, double count) {
        init(goodsModel, price, count);
    }

    public ComponentModel(GoodsModel goodsModel, GoodDT goodSMDT) {
        init(goodsModel, goodSMDT.getPriceAsIs(), goodSMDT.getSoldItems(), goodSMDT.getPriceBought());
    }

    private void init(GoodsModel goodsModel, double price, double count, double priceBought) {
        init(goodsModel, price, count);
       // if (goodsModel.isPurchase()) {
            this.price = priceBought;
       // }
    }

    private void init(GoodsModel goodsModel, double price, double count) {
        this.goodsModel = goodsModel;
       // regularPrice = goodsModel.getItemPrices().getRegularPrice();
        regularPrice = price;
        purchasePrice = goodsModel.getItemPrices().getMinPrice();
        this.count = count;
        edIzmMinValue = goodsModel.getEdIzm().getEdIzmMinVal();
        edIzmName = goodsModel.getEdIzm().getEdIzmName();
        countInStock = 0;

        if (goodsModel.isPurchase()) {
            this.price = goodsModel.getItemPrices().getMinPrice();
            purchasePrice = price;
        } else {
            this.price = price;
            purchasePrice = goodsModel.getItemPrices().getMinPrice();
        }
        double stockItems = goodsModel.getStock().getStockItems();

        boolean isInteger = edIzmMinValue == 1;
        startBalance = new BigDecimal(stockItems - (stockItems % edIzmMinValue)).setScale(isInteger ? 0 : 2, BigDecimal.ROUND_HALF_UP);

        if (purchasePrice == 0.0) {
            purchasePrice = price;
        }
        this.sum = this.count * this.price;
    }

    public void resetPrice(){
        price = regularPrice;
    }

    //----------------------- SETTERS -----------------------//

    public void setSum(double sum) {
        if(isMinValueUnit()){
            if(count > 0)
                price = sum / count;
        }else {
            if(price > 0)
                setCount(sum / price);
        }
        this.sum = sum;
    }

    public void setCount(double count) {
        this.count = count;
        if (goodsModel.isPurchase()) {
            countInStock = +count;
        } else {
            countInStock = -count;
        }
        sum = this.count * this.price;
    }

   public void setPrice(double price){
        this.price = price;
        sum = this.count * this.price;
    }

    //----------------------- GETTERS -----------------------//
    public double getSum() {
        //существуют ситуации типа setSum(1),а количество = 3, тогда цена = 0.(3)
        //если мы умножим цену на количество, то получится 3 * 0.(3) = 0.(9) < 1
        // и это черевато последствиями, надо округлять
        return Math.round(count * price);

    }

    public double getBaseSum() {
        //return Math.roundDown(count * price);
        return count * regularPrice;
    }


    public BigDecimal getSum(SaleMode saleMode) {
        if (saleMode == SaleMode.INVENTORY) {
            return getInventorySum();
        }
        return new BigDecimal(getSum());
    }

    public List<BigDecimal> getTerms() {
        if (terms.size() == 0) {
            terms.add(new BigDecimal(count));
            return terms;
        }
        List<BigDecimal> first = getTerms(terms, getDifference(terms, count)).toBlocking().first();
        Collections.reverse(first);
        return first;
    }

    public static Observable<BigDecimal> getDifference(List<BigDecimal> terms, double count) {
        return Observable.from(terms).reduce(BigDecimal::add)
                .zipWith(Observable.just(count), (bigDecimal, aDouble) -> {
                    return bigDecimal.subtract(new BigDecimal(aDouble));
                });
    }

    public static Observable<List<BigDecimal>> getTerms(List<BigDecimal> terms, Observable<BigDecimal> difference) {
        return Observable.just(terms)
                .zipWith(difference, Pair::create)
                .flatMap(pair -> {
                    Collections.reverse(pair.first);
                    final BigDecimal[] dif = {pair.second};
                    return Observable.from(pair.first)
                            .map(bigDecimal -> {
                                BigDecimal newDif = dif[0].subtract(bigDecimal).max(BigDecimal.ZERO);
                                BigDecimal subtract = bigDecimal.subtract(dif[0]);
                                dif[0] = newDif;
                                return subtract;
                            }).filter(bigDecimal -> bigDecimal.compareTo(BigDecimal.ZERO) > 0).toList();
                });
    }

    public BigDecimal getBalanceCount() {
        return new BigDecimal(goodsModel.getStock().getStockItems()).subtract(new BigDecimal(count));
    }

    public BigDecimal getInventorySum() {
        return new BigDecimal(price).multiply(getBalanceInequality());
    }

    public BigDecimal getSimpleSum() {
        return new BigDecimal(count * price).setScale(0, RoundingMode.DOWN);
    }

    GoodDT getGoodDT() {
        return new GoodDT(
                goodsModel.getItem().getFlower1CId(),
                regularPrice,
                count,
                price
        );
    }

    public BigDecimal getStartBalanceCount() {
        return startBalance;
    }

    public BigDecimal getBalanceInequality() {
        BigDecimal countBigDecimal = new BigDecimal(count);
        return countBigDecimal.subtract(startBalance);
    }

    //----------------------- MATH & LOGIC -----------------------//

    private double simple(double v) {
        return Math.floor(v * 100) / 100;
    }

    public boolean isNumberUnit(BigDecimal number){
        return number.compareTo(number.setScale(0, RoundingMode.DOWN)) == 0;
    }

    public boolean isMinValueUnit(){
        return isNumberUnit(new BigDecimal(edIzmMinValue));
    }

    public static double eps = 0.001;
    public static double getCertainIfNeeded(double val){
        return (Math.ceil(val) - val < eps) ? Math.ceil(val) : val;
    }

}
