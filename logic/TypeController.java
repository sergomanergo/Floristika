package ru.kazachkov.florist.logic;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableDouble;
import android.databinding.ObservableField;

import lombok.Getter;
import lombok.Setter;

public class TypeController {
    private Typer typer;
    private ObservableField<Integer> orderType;
    private ObservableBoolean isSale;
    private ObservableBoolean isShowcase;
    private ObservableBoolean isPrepayment;
    private ObservableBoolean isDelete;
    private ObservableBoolean isComeback;

    private ObservableBoolean fromCard;
    private ObservableDouble paidEditText;

    private boolean bankValue;
    @Getter
    @Setter
    private double initBankPaid;
    @Getter
    @Setter
    private double initPaid;

    private double todayBankPaid;
    private double todayPaid;
    private ObservableDouble comebackEditText;
    private double comebackBankPaid;
    private double comebackPaid;
    private double deleteValue;

    public TypeController(Typer typer,
                          ObservableField<Integer> orderType,
                          ObservableBoolean isSale,
                          ObservableBoolean isShowcase,
                          ObservableBoolean isPrepayment,
                          ObservableBoolean isDelete,
                          ObservableBoolean isComeback,
                          ObservableDouble paidEditText,
                          ObservableDouble comebackEditText,
                          ObservableBoolean fromCard) {
        this.typer = typer;
        this.orderType = orderType;
        this.isSale = isSale;
        this.isShowcase = isShowcase;
        this.isPrepayment = isPrepayment;
        this.isDelete = isDelete;
        this.isComeback = isComeback;
        this.paidEditText = paidEditText;
        this.comebackEditText = comebackEditText;
        this.fromCard = fromCard;
        bankValue = false;
        updateTypes();
    }

    public void setBankValue(boolean bankValue) {
        this.bankValue = bankValue;
        if (bankValue) {
            paidEditText.set(todayBankPaid);
        } else paidEditText.set(todayPaid);
    }

    private void updateTypes() {
        orderType.set(typer.getCurrentType());
        isSale.set(typer.getCurrentType() == Typer.SALE);
        isShowcase.set(typer.getCurrentType() == Typer.SHOWCASE);
        isPrepayment.set(typer.getCurrentType() == Typer.PREPAYMENT);
        isDelete.set(typer.getCurrentType() == Typer.DELETE);
        isComeback.set(typer.getCurrentType() == Typer.COMEBACK);
        fromCard.set(bankValue);
        if (bankValue) paidEditText.set(todayBankPaid);
        else paidEditText.set(todayPaid);
    }

    public void onNextType() {
        typer.onNextType();
        updateTypes();
    }

    public void setPaidValue(double value) {
        if (bankValue) {
            todayBankPaid = value;
        } else {
            todayPaid = value;
        }
    }

    public double getPaid() {
        return getTodayPaid();
    }

    public double getPaidBank() {
        return getTodayBankPaid();
    }

    public double getTodayPaid() {
        if (typer.getCurrentType() == Typer.SHOWCASE) return 0;
        return todayPaid;
    }

    public double getTodayBankPaid() {
        if (typer.getCurrentType() == Typer.SHOWCASE) return 0;
        return todayBankPaid;
    }

    public void setStartValue(double value) {
        setPaidValue(value);
        paidEditText.set(value);
    }

    public boolean isBankValue() {
        return bankValue;
    }

    public boolean isSale() {
        return isSale.get();
    }

    public int getCurentType() {
        return typer.getCurrentType();
    }

    public void setComebackValue(double value) {
        if (bankValue) {
            comebackBankPaid = value;
        } else {
            comebackPaid = value;
        }
    }

    public double getPaidForComeback() {
        return -comebackPaid;
    }

    public double getBankPaidForComeback() {
        return -comebackBankPaid;
    }

    public double getDeleteValue() {
        return -initPaid;
    }

    public double getDeleteBankValue() {
        return -initBankPaid;
    }
}
