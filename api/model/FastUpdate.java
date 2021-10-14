package ru.kazachkov.florist.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.kazachkov.florist.api.realm.RealmFakeItem;
import ru.kazachkov.florist.interfaces.ToRealmConvert;

@RequiredArgsConstructor
@AllArgsConstructor
public @Data
class FastUpdate {

    @SerializedName("ItemArray")
    private List<Item> items;

    @SerializedName("CategoriesArray")
    private List<Category> categories;

    @SerializedName("ItemsCategoriesArray")
    private List<ItemCategory> itemsCategories;

    @SerializedName("StoragesArray")
    private List<Storage> storages;

    @SerializedName("StockArray")
    private List<Stock> stocks;

    @SerializedName("ItemsPricesArray")
    private List<ItemPrices> itemsPrices;

    @SerializedName("EdIzmArray")
    private List<EdIzm> edIzmArray;

    @SerializedName("fakeItem")
    private String fakeItem;


    public static @Data
    class FastUpdateAuth {
        @SerializedName("authStatus")
        private AuthResult authResult;
        @SerializedName("baseData")
        private FastUpdate fastUpdate;
    }

    public List<ToRealmConvert> toRealmConverts() {
        List<ToRealmConvert> converts = new ArrayList<>();
        if (items != null) converts.addAll(items);
        if (categories != null) converts.addAll(categories);
        if (itemsCategories != null) converts.addAll(itemsCategories);
        if (storages != null) converts.addAll(storages);
        if (stocks != null) converts.addAll(stocks);
        if (itemsPrices != null) converts.addAll(itemsPrices);
        if (edIzmArray != null) converts.addAll(edIzmArray);
        if (fakeItem != null) converts.add(() -> new RealmFakeItem(fakeItem));
        return converts;
    }
}
