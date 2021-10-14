package ru.kazachkov.florist.order.paymentdata;

import java.math.BigDecimal;
import java.util.List;

import ru.kazachkov.florist.api.model.GoodDT;
import ru.kazachkov.florist.interfaces.BasePresenter;
import ru.kazachkov.florist.interfaces.BaseView;
import ru.kazachkov.florist.interfaces.OrderData;
import ru.kazachkov.florist.tools.SaleMode;

/**
 * Created by ishmukhametov on 13.10.16.
 */

public interface PaymentDataContract {

    interface View extends BaseView<Presenter> {

        void showDeliveryDialog(String received, String delivery);

        void showDeliveryResult(boolean isShowDelivery, boolean hideButtonDelivery);

        void showError(Throwable throwable);

        void showMessage(String message);

        void hideProgress();

        void showProgress();

        void showScreenAfterSendOrder();

        void updatePayment(Boolean state);
    }

    interface Presenter extends BasePresenter, DeliveryDialog.OnResultListener {
        void fullPaymentChanged(boolean isChanged);

        void calculateDelivery();

        String getComment();

        void setComment(String comment);


        void init(String basePrice, String totalPrice, List<GoodDT> listGoods, double discount);

    void init(String basePrice,String totalPrice, List<GoodDT> listGoods, OrderData orderData, double discount);

        List<String> getDiscountsList();

        void setDiscountPosition(int i);

        int getDiscountPosition();

        boolean isFullPayment();

        boolean isFinished();

        void setFullPayment(boolean fullPayment);

        void setFinished(boolean finished);

        boolean isFinishedVisible();

        void setFinishedVisible(boolean visible);

        String getReceived();

        double getBalance();

        String getInfoTextCalculation();

        void setInfoTextCalculation(String infoTextCalculation);

        String getPayment();

        String getDelivery();

        void setBasePrice(String basePrice);

        BigDecimal getBasePrice();

        BigDecimal getPrice();

        void setCash(String cash);

        void setCashless(String cashless);

        BigDecimal getCash();

        BigDecimal getCashless();

        Boolean getIsPayCash();

        void setIsPayCash(Boolean isPayCash);

        void prepareAndSendOrder(String user1CId, String bayer1CId, List<String> photosUri, SaleMode saleMode);

        public BigDecimal getPaidSumCashTotal();

        public BigDecimal getPaidSumBankTotal();

    }

}
