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
public class GetUsersAtWork {
    @Element(name = "soap12:Body")
    private RequestBody body;

    @lombok.Data
    @AllArgsConstructor
    public static class RequestBody {
        @Element(name = "GetUsersAtWork")
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

        @Element(name = "storageId")
        String storageId;

        @Element(name = "reportDate")
        String reportDate;

        @Element(name = "optionValue")
        int optionValue = -1;
    }

    public static GetUsersAtWork create(String sessionId, String storage1CID, String reportDate, int optV) {
        Data data = new Data(sessionId, storage1CID, reportDate, optV);
        RequestBody requestBody = new RequestBody(data);
        return new GetUsersAtWork(requestBody);
    }
}
