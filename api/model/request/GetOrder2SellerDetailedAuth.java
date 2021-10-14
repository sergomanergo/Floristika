package ru.kazachkov.florist.api.model.request;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

import lombok.AllArgsConstructor;
import lombok.Data;

@Root(name = "soap12:Envelope")
@NamespaceList({
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema-instance", prefix = "xsi"),
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema", prefix = "xsd"),
        @Namespace(prefix = "soap12", reference = "http://www.w3.org/2003/05/soap-envelope")
})
@Data
@AllArgsConstructor
public class GetOrder2SellerDetailedAuth {
    @Element(name = "soap12:Body")
    private RequestBody body;

    @lombok.Data
    @AllArgsConstructor
    public static class RequestBody {
        @Element(name = "GetOrder2SellerDetailedAuth")
        Data data;
    }

    @NamespaceList({
            @Namespace(reference = "http://reachmoreresearch.ru/flowers/")
    })
    @lombok.Data
    @AllArgsConstructor
    public static class Data {
        @Element(name = "sessionID")
        String sessionID;

        @Element(name = "storageId")
        String storageId;

        @Element(name = "order2Seller1cId")
        String order2Seller1cId;

        @Element(name = "order2SellerDate")
        String order2SellerDate;
    }

    public static GetOrder2SellerDetailedAuth create(String sessionId, String storage1CId, String order1CId, String orderCreatedDate) {
        Data data = new Data(sessionId, storage1CId, order1CId, orderCreatedDate);
        RequestBody requestBody = new RequestBody(data);
        return new GetOrder2SellerDetailedAuth(requestBody);
    }
}

