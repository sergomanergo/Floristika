package ru.kazachkov.florist.api.model.response;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kazachkov.florist.api.model.AuthResult;
import ru.kazachkov.florist.api.model.OrderSellerAtGlanceDT;
import ru.kazachkov.florist.interfaces.IOrderGlance;
import ru.kazachkov.florist.interfaces.OrderInfo;
import ru.kazachkov.florist.interfaces.PotentialErrorsProvider;
import ru.kazachkov.florist.interfaces.XmlFormated;


@Root(name = "soap:Envelope")
@NamespaceList({
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema-instance", prefix = "xsi"),
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema", prefix = "xsd"),
        @Namespace(prefix = "soap", reference = "http://www.w3.org/2003/05/soap-envelope")
})
@Data
@NoArgsConstructor
public class OrderSellerList implements XmlFormated, PotentialErrorsProvider {
    @Element(name = "Body")
    private RequestBody body;

    @Override
    public AuthResult getAuthResult() {
        return body.getResponse().getGetOrderListAuthResult().authStatus;
    }

    public OrderInfo getOrderInfo() {
        return new OrderSallerInfo(body.getResponse().getOrderListAuthResult.orderSellerListAtGlance);
    }


    @Data
    @NoArgsConstructor
    public static class RequestBody {
        @Element(name = "GetOrderSellerListAuthResponse")
        GetOrderSellerListAuthResponse response;
    }


    @NamespaceList({
            @Namespace(reference = "http://reachmoreresearch.ru/flowers/")
    })
    @Data
    @NoArgsConstructor
    public static class GetOrderSellerListAuthResponse {
        @Element(name = "GetOrderSellerListAuthResult")
        GetOrderListAuthResult getOrderListAuthResult;
    }


    @Data
    @NoArgsConstructor
    public static class GetOrderListAuthResult {
        @Element(name = "authStatus")
        AuthResult authStatus;

        @ElementList(name = "orderSellerListAtGlance", required = false)
        List<OrderSellerAtGlanceDT> orderSellerListAtGlance = new ArrayList<>();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderSallerInfo implements OrderInfo {
        List<OrderSellerAtGlanceDT> orderSellerListAtGlance;


        @Override
        public long getTotalOrderSum() {
            return 0;
        }

        @Override
        public long getTotalBankPaid() {
            return 0;
        }

        @Override
        public long getTotalCashPaid() {
            return 0;
        }


        @Override
        public List<IOrderGlance> getOrderList() {
            List<IOrderGlance> list = new ArrayList<>();
            list.addAll(orderSellerListAtGlance);
            return list;
        }
    }


}

