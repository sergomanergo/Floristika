package ru.kazachkov.florist.interfaces;

import java.util.List;

import ru.kazachkov.florist.api.model.WtdAction;
import ru.kazachkov.florist.logic.PaidHintData;

/**
 * Created by ishmukhametov on 01.06.16.
 */
public interface IOrderGlance {
    String getItemString();

    String getOrder1CId();

    String getResponciblePersonName();

    String getOrder1CDate();

    String getPaidStatusHint();

    String getClientName();

    String getClientId();

    int getPosition();

    int getGroup();

    void setPosition(int i);

    boolean isPurchase();

    int getDocType();

    PaidHintData getPaidDataHint();

    String getCommentString();

    List<WtdAction> getWtdActionsList();

    void setComment(String comment);
}
