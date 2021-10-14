package ru.kazachkov.florist.sales;

import android.widget.PopupWindow;

import ru.kazachkov.florist.tools.SaleMode;

/**
 * Created by ishmukhametov on 14.09.16.
 */

public class FabActionHandler {

    private final PopupWindow popupWindow;
    private SalesContract.Model model;

    public FabActionHandler(SalesContract.Model model, PopupWindow popupWindow) {
        this.model = model;
        this.popupWindow = popupWindow;
    }

    public void startInv() {
        model.startCompositionScreen(SaleMode.INVENTORY);
        popupWindow.dismiss();
    }

    public void startPurchase() {
        model.startCompositionScreen(SaleMode.PURCHASE);
        popupWindow.dismiss();
    }

    public void startWriteOff() {
        model.startCompositionScreen(SaleMode.WRITE_OFF);
        popupWindow.dismiss();
    }

    public void startDisplacement() {
        model.startCompositionScreen(SaleMode.DISPLACEMENT);
        popupWindow.dismiss();
    }

    public void startPrepay() {
        model.startCompositionScreen(SaleMode.PREPAY);
        popupWindow.dismiss();
    }

    public void startShowcase() {
        model.startCompositionScreen(SaleMode.SHOWCASE);
        popupWindow.dismiss();
    }

    public void startSale() {
        model.startCompositionScreen(SaleMode.SALE);
        popupWindow.dismiss();
    }

}
