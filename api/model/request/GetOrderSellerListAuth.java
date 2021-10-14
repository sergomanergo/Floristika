package ru.kazachkov.florist.api.model.request;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.kazachkov.florist.interfaces.OrdersRequestData;


@Root(name = "soap12:Envelope")
@NamespaceList({
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema-instance", prefix = "xsi"),
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema", prefix = "xsd"),
        @Namespace(prefix = "soap12", reference = "http://www.w3.org/2003/05/soap-envelope")
})
@Data
@AllArgsConstructor
public class GetOrderSellerListAuth implements OrdersRequestData {
    @Element(name = "soap12:Body")
    private RequestBody body;


    @lombok.Data
    @AllArgsConstructor
    public static class RequestBody {
        @Element(name = "GetOrderSellerListAuth")
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

        @Element(name = "dateBegin")
        String dateBegin;

        @Element(name = "dateEnd")
        String dateEnd;

        @Element(name = "storage1CId")
        String storage1CID;
    }

    public static GetOrderSellerListAuth create(String sessionId, String dateBegin, String dateEnd, String storage1CID) {
        return new GetOrderSellerListAuth(new RequestBody(new Data(sessionId, dateBegin, dateEnd, storage1CID)));
    }
}
