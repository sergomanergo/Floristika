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
public class EffectFastActionActivity4Response implements PotentialErrorsProvider {

    AuthResult authStatus;
    @SerializedName("orderDetailedData")
    OrderDetailedData4 orderDetailedData4;

    @Override
    public AuthResult getAuthResult() {
        return authStatus;
    }
}
