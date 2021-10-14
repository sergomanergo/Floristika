package ru.kazachkov.florist.logic;

import lombok.Getter;
import ru.kazachkov.florist.interfaces.PotentialErrorsProvider;


public class FloristResponse<T extends PotentialErrorsProvider> {
    @Getter
    T body;

    public FloristResponse(T t) {
        this.body = t;
    }

    public boolean isSuccessful() {
        return errorCode() > 0;
    }


    public int errorCode() {
        return body.getAuthResult().getResultId();
    }
}
