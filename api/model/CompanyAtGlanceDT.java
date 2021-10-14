package ru.kazachkov.florist.api.model;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.kazachkov.florist.api.realm.RealmCompany;
import ru.kazachkov.florist.interfaces.SpinnerText;
import ru.kazachkov.florist.interfaces.ToRealmConvert;

@AllArgsConstructor
@NoArgsConstructor
public class CompanyAtGlanceDT implements SpinnerText, ToRealmConvert {
    String company1CId;
    String companyName;
    int isBuyer;
    int isSeller;
    String telNumber;
    String cardId;
    Double discountVal;
    Double balanceVal;
    String clientComm;
    String storageId;

    @Override
    public String getSpinnerText() {
        return companyName;
    }

    public String getCompany1CId() {
        return company1CId;
    }

    public void setCompany1CId(String company1CId) {
        this.company1CId = company1CId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getIsBuyer() {
        return isBuyer;
    }

    public void setIsBuyer(int isBuyer) {
        this.isBuyer = isBuyer;
    }

    public int getIsSeller() {
        return isSeller;
    }

    public void setIsSeller(int isSeller) {
        this.isSeller = isSeller;
    }

    public String getStorageId() {
        return storageId;
    }

    public void setStorageId(String storageId) {
        this.storageId = storageId;
    }

    public String getTelNumber() {
        return telNumber;
    }

    @Override
    public RealmCompany convertToFunc() {
        return RealmCompany.newInstance(this);
    }
}
