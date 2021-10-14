package ru.kazachkov.florist.api.model.response;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kazachkov.florist.api.model.AuthResult;
import ru.kazachkov.florist.api.model.OrderDetailed;
import ru.kazachkov.florist.interfaces.PotentialErrorsProvider;

@Data
@NoArgsConstructor
public class OrderDetailedJS implements PotentialErrorsProvider {

    AuthResult authStatus;
    @SerializedName("orderDetailedData")
    OrderDetailed orderDetailed;

    @Override
    public AuthResult getAuthResult() {
        return authStatus;
    }
}
