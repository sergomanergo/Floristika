package ru.kazachkov.florist.api.realm;

import io.realm.RealmObject;
import ru.kazachkov.florist.api.model.CompanyAtGlanceDT;
import ru.kazachkov.florist.interfaces.FromRealmConvert;

/**
 * Created by ishmukhametov on 09.12.16.
 */

public class RealmCompany extends RealmObject implements FromRealmConvert<CompanyAtGlanceDT> {
    private String company1CId;
    private String companyName;
    private int isBuyer;
    private int isSeller;
    private String telNumber;
    private String cardId;
    private Double discountVal;
    private Double balanceVal;
    private String clientComm;
    private String storageId;

    public static RealmCompany newInstance(CompanyAtGlanceDT companyAtGlanceDT) {
        RealmCompany realmCompany = new RealmCompany();
        realmCompany.setCompany1CId(companyAtGlanceDT.getCompany1CId());
        realmCompany.setCompanyName(companyAtGlanceDT.getCompanyName());
        realmCompany.setIsBuyer(companyAtGlanceDT.getIsBuyer());
        realmCompany.setIsSeller(companyAtGlanceDT.getIsSeller());
        realmCompany.setStorageId(companyAtGlanceDT.getStorageId());
        realmCompany.setTelNumber(companyAtGlanceDT.getTelNumber());
        return realmCompany;
    }

    public RealmCompany() {
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

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }


    @Override
    public CompanyAtGlanceDT fromRealm() {
        return new CompanyAtGlanceDT(company1CId, companyName, isBuyer, isSeller, telNumber, cardId, discountVal, balanceVal, clientComm, storageId);
    }
}
