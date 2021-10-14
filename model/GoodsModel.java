package ru.kazachkov.florist.model;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.kazachkov.florist.api.model.Category;
import ru.kazachkov.florist.api.model.EdIzm;
import ru.kazachkov.florist.api.model.Item;
import ru.kazachkov.florist.api.model.ItemPrices;
import ru.kazachkov.florist.api.model.Photo;
import ru.kazachkov.florist.api.model.Stock;
import ru.kazachkov.florist.interfaces.Searchable;
import ru.kazachkov.florist.tools.SaleMode;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class GoodsModel implements Searchable {
    @NonNull
    private Item item;
    @NonNull
    private ItemPrices itemPrices;
    @NonNull
    private List<Photo> photo;
    @NonNull
    private EdIzm edIzm;
    @NonNull
    private List<Category> categories;
    @NonNull
    private Stock stock;

    @Getter
    @Setter
    private SaleMode saleMode;

    int searchPointCount;

    private String link = "http://flower-premium.ru/files/products/tulraz.800x600w.jpg?d0c103e3130b8f3cd670671f997b8e63";

    @Override
    public void addPoint() {
        searchPointCount++;
    }

    @Override
    public int getPointsCount() {
        return searchPointCount;
    }

    @Override
    public String getName() {
        return item.getFlowerName();
    }

    public boolean isPurchase() {
        return SaleMode.PURCHASE == saleMode;
    }

    public int getInStock() {
        if (stock == null) return -1;
        return stock.getInStock();
    }
}
