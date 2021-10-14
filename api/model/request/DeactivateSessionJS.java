package ru.kazachkov.florist.api.model.request;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Root(name = "soap12:Envelope")
@NamespaceList({
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema-instance", prefix = "xsi"),
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema", prefix = "xsd"),
        @Namespace(prefix = "soap12", reference = "http://www.w3.org/2003/05/soap-envelope")
})
@Data
@AllArgsConstructor
public class DeactivateSessionJS {
    @Element(name = "soap12:Body")
    private RequestBody body;

    @lombok.Data
    @AllArgsConstructor
    public static class RequestBody {
        @Element(name = "DeactivateSessionJS")
        Data data;
    }

    @NamespaceList({
            @Namespace(reference = "http://reachmoreresearch.ru/flowers/")
    })
    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Data {
        @Element(name = "sessionId")
        String sessionId;
    }

    public static DeactivateSessionJS create(String sessionId) {
        RequestBody requestBody = new RequestBody(new Data(sessionId));
        return new DeactivateSessionJS(requestBody);
    }
}
