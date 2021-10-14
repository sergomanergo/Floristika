package ru.kazachkov.florist.api.model.response;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kazachkov.florist.api.model.AuthResult;
import ru.kazachkov.florist.api.model.Order2Seller;
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
public class OrderSellerDetaildAuth implements XmlFormated, PotentialErrorsProvider {
    @Element(name = "Body")
    private RequestBody body;

    @Override
    public AuthResult getAuthResult() {
        return body.getResponse().getGetOrderListAuthResult().authStatus;
    }

    @Data
    @NoArgsConstructor
    public static class RequestBody {
        @Element(name = "GetOrder2SellerDetailedAuthResponse")
        GetOrderSellerListAuthResponse response;
    }

    @NamespaceList({
            @Namespace(reference = "http://reachmoreresearch.ru/flowers/")
    })
    @Data
    @NoArgsConstructor
    public static class GetOrderSellerListAuthResponse {
        @Element(name = "GetOrder2SellerDetailedAuthResult")
        GetOrderListAuthResult getOrderListAuthResult;
    }

    @Data
    @NoArgsConstructor
    public static class GetOrderListAuthResult {
        @Element(name = "authStatus")
        AuthResult authStatus;

        @Element(name = "order2SellerDetailedData")
        Order2Seller order2Seller;
    }
}

