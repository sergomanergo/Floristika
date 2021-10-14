package ru.kazachkov.florist.model.impl;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.kazachkov.florist.interfaces.IOrderGlance;
import ru.kazachkov.florist.model.md.SaleModel;
import ru.kazachkov.florist.tools.Utils;

@Data
@RequiredArgsConstructor
public class SaleModelImpl implements SaleModel {
    @NonNull
    IOrderGlance orderGlance;

    @Override
    public String content() {
        return orderGlance.getItemString();
    }

    @Override
    public String number() {
        return orderGlance.getOrder1CId();
    }

    @Override
    public String author() {
        return orderGlance.getResponciblePersonName();
    }

    @Override
    public String date() {
        return date("dd.MM");
        /*String date =  orderGlance.getOrder1CDate().substring(0, 10);
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
        try {
            Date result =  df.parse(date);
            DateFormat newDf = new SimpleDateFormat("dd.mm.yyyy", Locale.ENGLISH);
            return newDf.format(result);
        }catch (Exception e){

        }
        return null;*/
    }

    @Override
    public DateTime dateTime() {
        return Utils.dateTimeOf(orderGlance.getOrder1CDate());
    }

    @Override
    public String date(String format) {
        return Utils.prepareDate(orderGlance.getOrder1CDate(), format);
    }

    @Override
    public String price() {
        return orderGlance.getPaidStatusHint();
    }

    @Override
    public String getComment() {
        return orderGlance.getCommentString();
    }

    @Override
    public int getDocType() {
        return orderGlance.getDocType();
    }

    @Override
    public String getId() {
        return orderGlance.getOrder1CId();
    }

    @Override
    public void setComment(String commentString) {
        orderGlance.setComment(commentString);
    }
}
