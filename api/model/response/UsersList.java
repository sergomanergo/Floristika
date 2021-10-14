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
import ru.kazachkov.florist.api.model.User1CIdDT;
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
public class UsersList implements XmlFormated, PotentialErrorsProvider {
    @Element(name = "Body")
    private RequestBody body;

    @Override
    public AuthResult getAuthResult() {
        return body.getGetOrderListAuthResponse().getGetOrderListAuthResult().authStatus;
    }


    @Data
    @NoArgsConstructor
    public static class RequestBody {
        @Element(name = "GetUsersAtWorkResponse")
        GetOrderListAuthResponse getOrderListAuthResponse;
    }


    @NamespaceList({
            @Namespace(reference = "http://reachmoreresearch.ru/flowers/")
    })
    @Data
    @NoArgsConstructor
    public static class GetOrderListAuthResponse {
        @Element(name = "GetUsersAtWorkResult")
        GetOrderListAuthResult getOrderListAuthResult;
    }


    @Data
    @NoArgsConstructor
    public static class GetOrderListAuthResult {
        @Element(name = "authStatus")
        AuthResult authStatus;

        @ElementList(name = "staffData", required = false)
        List<User1CIdDT> staffData;
    }
}
