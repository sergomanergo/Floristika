package ru.kazachkov.florist.api.model.request;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.kazachkov.florist.api.model.OrderDetailed;

@Root(name = "soap12:Envelope")
@NamespaceList({
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema-instance", prefix = "xsi"),
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema", prefix = "xsd"),
        @Namespace(prefix = "soap12", reference = "http://www.w3.org/2003/05/soap-envelope")
})
@Data
@AllArgsConstructor
public class AddUpdateOrderPhotoAuth {
    @Element(name = "soap12:Body")
    private RequestBody body;

    @lombok.Data
    @AllArgsConstructor
    public static class RequestBody {
        @Element(name = "AddUpdateOrderPhotoAuth")
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

        @Element(name = "storage1CId")
        String storage1CID;

        @Element(name = "orderDetailed")
        OrderDetailed orderDetailed;
    }

    public static AddUpdateOrderPhotoAuth create(String sessionId, String storage1CID, OrderDetailed orderDetailed) {
        Data data = new Data(sessionId, storage1CID, orderDetailed);
        RequestBody requestBody = new RequestBody(data);
        return new AddUpdateOrderPhotoAuth(requestBody);
    }
}

