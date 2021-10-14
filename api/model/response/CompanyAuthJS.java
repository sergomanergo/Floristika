package ru.kazachkov.florist.api.model.response;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kazachkov.florist.api.model.AuthResult;
import ru.kazachkov.florist.api.model.CompanyAtGlanceDT;
import ru.kazachkov.florist.interfaces.PotentialErrorsProvider;


@Data
@NoArgsConstructor
public class CompanyAuthJS implements PotentialErrorsProvider {
    AuthResult authStatus;
    List<CompanyAtGlanceDT> companyData;

    @Override
    public AuthResult getAuthResult() {
        return authStatus;
    }
}

