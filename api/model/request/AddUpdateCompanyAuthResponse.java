package ru.kazachkov.florist.api.model.request;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.kazachkov.florist.api.model.AuthResult;


@Root(name = "soap12:Envelope")
@NamespaceList({
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema-instance", prefix = "xsi"),
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema", prefix = "xsd"),
        @Namespace(prefix = "soap12", reference = "http://www.w3.org/2003/05/soap-envelope")
})
@Data
public class AddUpdateCompanyAuthResponse {
    @Element(name = "soap12:Body")
    private RequestBody body;

    @lombok.Data
    public static class RequestBody {
        @Element(name = "AddUpdateCompanyAuthResponse")
        Data data;
    }

    @NamespaceList({
            @Namespace(reference = "http://reachmoreresearch.ru/flowers/")
    })
    @lombok.Data
    public static class Data {
        @Element(name = "addUpdateCompanyAuthResult")
        private AddUpdateCompanyAuthResult addUpdateCompanyAuthResult;
    }


    public static class AddUpdateCompanyAuthResult {
        @Element(name = "authStatus")
        AuthResult authStatus;
    }

}
