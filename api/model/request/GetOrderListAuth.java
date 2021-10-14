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
public class GetOrderListAuth implements OrdersRequestData {
    @Element(name = "soap12:Body")
    private RequestBody body;


    @lombok.Data
    @AllArgsConstructor
    public static class RequestBody {
        @Element(name = "GetOrderListAuth4")
        Data data;
    }

    @NamespaceList({
            @Namespace(reference = "http://reachmoreresearch.ru/flowers/")
    })
    @lombok.Data
    @AllArgsConstructor
    public static class Data {
        @Element(name = "sessionId")
        String sessionId;

        /*@Element(name = "reportDate")
        String dateTimeStart;*/

        @Element(name = "reportDateStart")
        String dateTimeStart;

        @Element(name = "reportDateFinish")
        String dateTimeFinish;

        @Element(name = "storage1CID")
        String storage1CID;

        @Element(name = "listOption")
        int listOption;
    }

    public static GetOrderListAuth create(String sessionId, String dateTimeStart, String dateTimeFinish, String storage1CID, int listOption) {
        return new GetOrderListAuth(new RequestBody(new Data(sessionId, dateTimeStart, dateTimeFinish, storage1CID, listOption)));
    }
}
