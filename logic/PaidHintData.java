package ru.kazachkov.florist.logic;

import java.math.BigDecimal;
import java.text.NumberFormat;

import ru.kazachkov.florist.tools.Utils;

/**
 * Created by ishmukhametov on 08.12.16.
 */

public class PaidHintData {

    private final String sumFinal;

    private final String paidSumCashTotal;
    private final String paidSumCashToday;
    private final String paidSumBankTotal;
    private final String paidSumBankToday;

    public PaidHintData(String sumFinal, String paidSumCashTotal, String paidSumCashToday, String paidSumBankTotal, String paidSumBankToday) {
        this.sumFinal = sumFinal;
        this.paidSumCashTotal = paidSumCashTotal;
        this.paidSumCashToday = paidSumCashToday;
        this.paidSumBankTotal = paidSumBankTotal;
        this.paidSumBankToday = paidSumBankToday;
    }

    public String getHintColoredHtmlString() {
        BigDecimal total = new BigDecimal(paidSumBankTotal).add(new BigDecimal(paidSumCashTotal));
        BigDecimal today = new BigDecimal(paidSumBankToday).add(new BigDecimal(paidSumCashToday));

        NumberFormat currencyFormat = Utils.getCurrencyFormat();
        String sumFinalString = currencyFormat.format(new BigDecimal(sumFinal)) + " р.";
        if (new BigDecimal(sumFinal).compareTo(total) <= 0) {
            if (total.compareTo(new BigDecimal(0)) != 0) {
                return "<font color=#ad1457>" + sumFinalString + "</font>";
            } else {
                return "<font color=#424242>" + sumFinalString + "</font>";
            }
        } else {
            String balanceString = currencyFormat.format(total);

            String sumHtml = "<font color=#424242> из " + sumFinalString + "</font>";
            if (today.compareTo(new BigDecimal(0)) != 0) {
                return "<font color=#ad1457>" + balanceString + "</font><br>" + sumHtml;
            } else {
                return "<font color=#424242>" + balanceString + "</font><br>" + sumHtml;
            }
        }
    }


    public BigDecimal getPaidSum() {
        return new BigDecimal(paidSumBankTotal).add(new BigDecimal(paidSumCashTotal));
    }

    public BigDecimal getTotalSum() {
        return new BigDecimal(sumFinal);
    }
}
