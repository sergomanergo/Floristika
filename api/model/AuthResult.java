package ru.kazachkov.florist.api.model;

import org.simpleframework.xml.Element;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public @Data
class AuthResult {
    @Element(name = "resultId")
    private int resultId;
    @Element(name = "msgValue")
    private String msgValue;

    public boolean isSuccessful() {
        return resultId > 0;
    }

    public int code() {
        return resultId;
    }
}
