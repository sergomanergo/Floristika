package ru.kazachkov.florist.api.model;

import org.simpleframework.xml.Element;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kazachkov.florist.interfaces.IOrderGlance;
import ru.kazachkov.florist.logic.PaidHintData;

@AllArgsConstructor
@NoArgsConstructor
public @Data
class OrderSellerAtGlanceDT implements IOrderGlance {
    @Element(name = "orderSeqNum")
    private int orderSeqNum;
    @Element(name = "order1CId")
    private String order1CId;
    @Element(name = "order1CDate")
    private String order1CDate;
    @Element(name = "sumFinal")
    private String sumFinal;
    @Element(name = "commentString", required = false)
    private String commentString;
    @Element(name = "itemString")
    private String itemString;
    @Element(name = "sellerName")
    private String sellerName;
    @Element(name = "docType")
    private int docType;

    private int position;


    @Override
    public void setComment(String comment) {
        commentString = comment;
    }

    @Override
    public String getResponciblePersonName() {
        return sellerName;
    }

    @Override
    public String getPaidStatusHint() {
        return String.valueOf(sumFinal);
    }

    @Override
    public String getClientName() {
        return sellerName;
    }

    @Override
    public String getClientId() {
        return ""; //todo: пока пусто
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public int getGroup() {
        return 0;
    }

    @Override
    public void setPosition(int i) {
        position = i;
    }

    @Override
    public boolean isPurchase() {
        return true;
    }

    @Override
    public PaidHintData getPaidDataHint() {
        return new PaidHintData(sumFinal, "0.0", "0.0", "0.0", sumFinal);
    }


    @Override
    public List<WtdAction> getWtdActionsList() {
        return new ArrayList<>();
    }
}
