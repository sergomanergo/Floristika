package ru.kazachkov.florist.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kazachkov.florist.api.model.AuthResult;
import ru.kazachkov.florist.api.model.Item;
import ru.kazachkov.florist.interfaces.PotentialErrorsProvider;


@Data
@NoArgsConstructor
public class ItemDataJS implements PotentialErrorsProvider {

    AuthResult authStatus;
    Item itemData;

    @Override
    public AuthResult getAuthResult() {
        return authStatus;
    }
}
