package ru.kazachkov.florist.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kazachkov.florist.api.model.AuthResult;
import ru.kazachkov.florist.api.model.CompanyAtGlanceDT;


@Data
@NoArgsConstructor
public class CompaniesList {
    AuthResult authStatus;

    @SerializedName("companyData")
    List<CompanyAtGlanceDT> ordersReportData;
}
