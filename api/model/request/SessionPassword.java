package ru.kazachkov.florist.api.model.request;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

import lombok.Data;

@Root(name = "soap12:Envelope")
@NamespaceList({
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema-instance", prefix = "xsi"),
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema", prefix = "xsd"),
        @Namespace(prefix = "soap12", reference = "http://www.w3.org/2003/05/soap-envelope")
})
@Data
public class SessionPassword {
    @Element(name = "soap12:Body")
    private RequestBody body;

    public SessionPassword(RequestBody body) {
        this.body = body;
    }


    @lombok.Data
    public static class RequestBody {
        @Element(name = "CheckSessionPasswordJS4")
        Data data;

        public RequestBody(Data data) {
            this.data = data;
        }
    }

    @NamespaceList({
            @Namespace(reference = "http://reachmoreresearch.ru/flowers/")
    })
    @lombok.Data
    public static class Data {
        @Element(name = "sessionId")
        String sessionId;

        @Element(name = "pwdValue")
        String pwdValue;

        public Data(String sessionId, String pwdValue) {
            this.sessionId = sessionId;
            this.pwdValue = pwdValue;
        }
    }

    public static SessionPassword create(String password, String sessionId) {
        return new SessionPassword(new RequestBody(new Data(sessionId, password)));
    }
}
