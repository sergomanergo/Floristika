package ru.kazachkov.florist.api.model.request;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.kazachkov.florist.api.model.Order2Seller;

@Root(name = "soap12:Envelope")
@NamespaceList({
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema-instance", prefix = "xsi"),
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema", prefix = "xsd"),
        @Namespace(prefix = "soap12", reference = "http://www.w3.org/2003/05/soap-envelope")
})
@Data
@AllArgsConstructor
public class AddUpdateOrder2SellerAuth {
    @Element(name = "soap12:Body")
    private RequestBody body;

    @lombok.Data
    @AllArgsConstructor
    public static class RequestBody {
        @Element(name = "AddUpdateOrder2SellerAuth")
        Data data;
    }

    @NamespaceList({
            @Namespace(reference = "http://reachmoreresearch.ru/flowers/")
    })
    @lombok.Data
    @AllArgsConstructor
    public static class Data {
        @Element(name = "sessionID")
        String sessionId;

        @Element(name = "storage1CID")
        String storage1CID;

        @Element(name = "order2Seller")
        Order2Seller order2Seller;
    }

    public static AddUpdateOrder2SellerAuth create(String sessionId, String storage1CID, Order2Seller order2Seller) {
        Data data = new Data(sessionId, storage1CID, order2Seller);
        RequestBody requestBody = new RequestBody(data);
        return new AddUpdateOrder2SellerAuth(requestBody);
    }
}

