package ru.kazachkov.florist.api.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;

import java.util.List;

import lombok.Data;
import lombok.Builder;

/**
 * Created by Dinis Ishmukhametov on 03.09.17.
 * dinis.ish@gmail.com
 */

@Data
@Builder
public class OrderData2Save {

    @Element(name = "order1CId")
    @Builder.Default
    String order1CId = "";

    @Element(name = "order1CDate")
    String orderDate;

    @Element(name = "storage1CId", required = false)
    String storage1CId;

    @Element(name = "docType")
    @Builder.Default
    int docType = -1;

    @Element(name = "commentString", required = false)
    @Builder.Default
    String commentString = "";

    @Element(name = "sumFinal")
    @Builder.Default
    double sumFinal =  0.0;

    @Element(name = "paidSumCashToday")
    @Builder.Default
    double paidSumCashToday =  0.0;

    @Element(name = "paidSumBankToday")
    @Builder.Default
    double paidSumBankToday =  0.0;

    @Element(name = "responciblePersonName")
    String responciblePersonName;

    @Element(name = "isFlowerServiceAuto")
    int isFlowerServiceAuto;

    @Element(name = "saleOrderType")
    int saleOrderType;

    @Element(name = "secondPart1CId")
    String secondPart1CId;

    @Element(name = "orderDiscount")
    double orderDiscount;

    /**
     * Public isChequed As Integer = 0 - признак "выдан чек" (первоначально устанавливается на клиенте пользователем)
     * Public isFinished As Integer = 0 - признак "заказ выполнен" (доступен на клиенте, если признак "есть предоплата" равен 1 и общая сумма оплат с учетом текущей равна сумме заказа с точностью до 2-х рублей)
     * Public isPrepaid As Integer = 0 - признак "есть предоплата" (значение либо передается для ранее сохраненного заказа, либо устанавливается пользователем на клиенте. Если сумма оплат менее итоговой суммы на 2 руб, то всегда присваиваем значение 1)
     */

    @Element(name = "isChequed")
    int isChequed;

    @Element(name = "isFinished")
    int isFinished;

    @Element(name = "isPrepaid")
    int isPrepaid;

    @Path("goods")
    @ElementList(inline = true, entry = "GoodSMDiscDT")
    List<GoodSMDiscDT> goods;

}
