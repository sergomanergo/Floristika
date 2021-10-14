package ru.kazachkov.florist.api.model.response;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kazachkov.florist.api.model.AuthResult;
import ru.kazachkov.florist.api.model.OrderDetailedData4;
import ru.kazachkov.florist.interfaces.PotentialErrorsProvider;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EffectFastActionActivityResponse implements PotentialErrorsProvider {

    AuthResult authStatus;
    @SerializedName("orderDetailedData")
    OrderDetailedData4 orderDetailedData;

    @Override
    public AuthResult getAuthResult() {
        return authStatus;
    }
}
