package ru.kazachkov.florist.api.model.request;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;

@Root(name = "soap12:Envelope")
@NamespaceList({
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema-instance", prefix = "xsi"),
        @Namespace(reference = "http://www.w3.org/2001/XMLSchema", prefix = "xsd"),
        @Namespace(prefix = "soap12", reference = "http://www.w3.org/2003/05/soap-envelope")
})
@Data
@AllArgsConstructor
public class EffectFastActionActivity4 {
    @Element(name = "soap12:Body")
    private RequestBody body;

    @lombok.Data
    @AllArgsConstructor
    public static class RequestBody {
        @Element(name = "EffectFastActionActivity4DateStr")
        Data data;
    }

    @Builder
    @NamespaceList({
            @Namespace(reference = "http://reachmoreresearch.ru/flowers/")
    })
    @lombok.Data
    @AllArgsConstructor
    public static class Data {
        @Element(name = "sessionId")
        String sessionId;

        @Element(name = "storage1CId")
        String storage1CId;

        @Element(name = "docType")
        int docType;

        @Element(name = "fastActionType")
        int fastActionType;

        @Element(name = "orderDateStr")
        String orderDate;

        @Element(name = "order1CId")
        String order1CId;
    }

    public static EffectFastActionActivity4 create(Data data) {
        RequestBody requestBody = new RequestBody(data);
        return new EffectFastActionActivity4(requestBody);
    }
}

