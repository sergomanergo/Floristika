package ru.kazachkov.florist.order.sellerdata;

import android.support.annotation.Nullable;

import java.math.BigDecimal;
import java.util.List;

import ru.kazachkov.florist.api.model.Category;
import ru.kazachkov.florist.api.model.CompanyAtGlanceDT;
import ru.kazachkov.florist.api.model.User1CIdDT;
import ru.kazachkov.florist.interfaces.BasePresenter;
import ru.kazachkov.florist.interfaces.BaseView;
import ru.kazachkov.florist.interfaces.OrderData;
import ru.kazachkov.florist.order.paymentdata.DeliveryDialog;
import ru.kazachkov.florist.tools.SaleMode;
import rx.Observable;

public interface SellerDataContract {

    interface View extends BaseView<Presenter> {
        void hideSearchPanel();

        void showAddClientDialog();

        void showFirstCompanyInSellerData(CompanyAtGlanceDT name);

        void showErrorLoadingCompanies();

        void showAuthors(List<User1CIdDT> user1CIdDTs, int currentAuthorPosition);

        void showError(Throwable throwable);

        void showClients(List<CompanyAtGlanceDT> companyAtGlanceDTs);

        Observable<CharSequence> getSearchObs();

        void showSearchPanel();

        void showSearchViewWithText(CompanyAtGlanceDT currentClient);

        void showClientInfo(CompanyAtGlanceDT client);

        void showCategories(List<Category> categories);

    }

    interface Presenter extends BasePresenter, AddClientDialog.OnResultListener {
        void accountingChanged(boolean isChecked);

        void addNewClient();

        void setSaleMode(SaleMode saleMode);


        List<User1CIdDT> getAuthors();

        void setSelectedAuthorPosition(int i);

        void setSelectedResponsiblePosition(int i);

        String getBasePrice();

        void setBasePrice(BigDecimal basePrice);

        String getAuthorId();

        String getCategoryId();

        void selectClient(CompanyAtGlanceDT client);

        void setSelectedAuthor(User1CIdDT item);

        void stopSearch();

        void startSearch();

        void setCategory(Category category);

        void init(@Nullable OrderData orderData);

        void refreshSubscriptions(Boolean fromRemote);

        String getClient1cId();
    }

}
