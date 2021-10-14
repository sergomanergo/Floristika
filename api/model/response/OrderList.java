package ru.kazachkov.florist.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kazachkov.florist.api.model.AuthResult;
import ru.kazachkov.florist.api.model.OrderGlance;
import ru.kazachkov.florist.interfaces.IOrderGlance;
import ru.kazachkov.florist.interfaces.OrderInfo;
import ru.kazachkov.florist.interfaces.PotentialErrorsProvider;


@Data
@NoArgsConstructor
public class OrderList implements PotentialErrorsProvider {
    @SerializedName("authStatus")
    AuthResult authStatus;

    @SerializedName("ordersReportData")
    OrdersReportData ordersReportData;

    @Override
    public AuthResult getAuthResult() {
        return authStatus;
    }


    @NoArgsConstructor
    public static class OrdersReportData implements OrderInfo {
        long totalOrderSum;
        long totalBankPaid;
        long totalCashPaid;

        List<OrderGlance> orderList;

        @Override
        public long getTotalOrderSum() {
            return totalOrderSum;
        }

        @Override
        public long getTotalBankPaid() {
            return totalBankPaid;
        }

        @Override
        public long getTotalCashPaid() {
            return totalCashPaid;
        }

        @Override
        public List<IOrderGlance> getOrderList() {
            List<IOrderGlance> list = new ArrayList<>();
            if(orderList != null)
               list.addAll(orderList);
            return list;
        }
    }
}
