package ru.kazachkov.florist.composition;


import android.app.Activity;
import android.support.annotation.StringRes;

import java.math.BigDecimal;
import java.util.List;

import ru.kazachkov.florist.api.model.GoodDT;
import ru.kazachkov.florist.interfaces.BasePresenter;
import ru.kazachkov.florist.interfaces.BaseView;
import ru.kazachkov.florist.interfaces.BindDetailed;
import ru.kazachkov.florist.model.GoodsModel;
import ru.kazachkov.florist.model.SaleComposModel;
import ru.kazachkov.florist.sales.EFastAction;
import ru.kazachkov.florist.tools.SaleMode;
import ru.kazachkov.florist.viewmodel.impl.ComponentVMImpl;
import ru.kazachkov.florist.viewmodel.impl.GoodsVMImpl;
import ru.kazachkov.florist.viewmodel.impl.SaleComposVMImpl;

public interface CompositionContract {
    interface Presenter extends BasePresenter, GoodsVMImpl.Callback {
        void startCashOrder();

        void startCashlessOrder();

        void startCommonOrder();

        void startSelectOrderVariant();

        void setOrderAction(EFastAction orderAction);

        void saveOrder();

        void updateGoodsModel(String flowerId);
    }

    interface View extends BaseView<CompositionContract.Presenter> {
        void showAddGoodsScreen();

        void showHomeTitle(String text);

        void showFabGroup(EFastAction orderAction);

        Activity getContext();

        void showAmountOf(BigDecimal value);

        void showOldSum(BigDecimal value);

        void showDifferenceOf(BigDecimal total, BigDecimal current);


        void backToMainScreen();

        void showError(Throwable throwable);

        void showToast(@StringRes int stringId);

        void clearFocusSearchView();

        void showScreenOrder(List<GoodDT> goodsList, String basePrice,  String totalPrice,SaleMode saleMode, BindDetailed orderDetailed, Integer isPayTypeCash, double discount);

        void showGoods(List<GoodsModel> goodsModels);

        void showEditGoodsScreen(String flower1CId);

        void hideFabsGroup();

        void showInventoryDialog(InventoryCountDialog.OnResultListener onResultListener, String name, BigDecimal price, List<BigDecimal> terms);

        void showCountDialog(SaleComposModel scModel,
                             GoodsModel goodsModel,
                             ComponentVMImpl componentVM,
                             BigDecimal count,
                             BigDecimal price,
                             boolean createComponent);

        void showPhotosScreen(List<String> photos, int position);

        void showProgress();

        void hideProgress();

        //--new--//
        void hideSoftKeyboard();
    }
}
