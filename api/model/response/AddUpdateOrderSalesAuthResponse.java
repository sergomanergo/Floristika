package ru.kazachkov.florist.api.model.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kazachkov.florist.api.model.AuthResult;


@Data
@NoArgsConstructor
public class AddUpdateOrderSalesAuthResponse {


    AuthResult authStatus;

    List<Stock> goods;

    String order1CId;

    String orderDate;

    int docType;

    class Stock{
        private String storage1CId;
        private String flower1CId;
        private double stockItems;
        private double inStock;
        private double priceRetail;
        private double priceBought;
    }
}
