package ru.kazachkov.florist.api.model.response;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kazachkov.florist.api.model.AuthResult;
import ru.kazachkov.florist.api.model.ItemPrices;
import ru.kazachkov.florist.api.model.Stock;
import ru.kazachkov.florist.interfaces.PotentialErrorsProvider;
import ru.kazachkov.florist.interfaces.XmlFormated;


@Root(name = "soap:Envelope", strict = false)
@NamespaceList({
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema-instance", prefix = "xsi"),
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema", prefix = "xsd"),
        @Namespace(prefix = "soap", reference = "http://www.w3.org/2003/05/soap-envelope")
})
@Data
@NoArgsConstructor
public class AddUpdateOrder2SellerResponse implements XmlFormated, PotentialErrorsProvider {
    @Element(name = "Body")
    private RequestBody body;

    @Override
    public AuthResult getAuthResult() {
        return body.getResponse().getContent().authStatus;
    }


    @Data
    @NoArgsConstructor
    public static class RequestBody {
        @Element(name = "AddUpdateOrder2SellerAuthResponse")
        Response response;
    }


    @NamespaceList({
            @Namespace(reference = "http://reachmoreresearch.ru/flowers/")
    })
    @Data
    @NoArgsConstructor
    public static class Response {
        @Element(name = "AddUpdateOrder2SellerAuthResult")
        Content content;
    }


    @Data
    @NoArgsConstructor
    public static class Content {
        @Element(name = "authStatus")
        AuthResult authStatus;

        @ElementList(name = "stockItems", required = false)
        List<Stock> stockItems;

        @ElementList(name = "priceItems", required = false)
        List<ItemPrices> priceItems;

        @Element(name = "orderShortData")
        OrderShortData orderShortData;


    }

    @Data
    @NoArgsConstructor
    public static class OrderShortData {
        @Element(name = "order1CId")
        String order1CId;
        @Element(name = "orderDate")
        String orderDate;
        @Element(name = "docType")
        int docType;
    }
}
