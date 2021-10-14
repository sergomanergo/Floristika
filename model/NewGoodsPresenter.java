package ru.kazachkov.florist.model;


import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ru.kazachkov.florist.NewGoodsActivity;
import ru.kazachkov.florist.api.model.Category;
import ru.kazachkov.florist.api.model.EdIzm;
import ru.kazachkov.florist.api.model.Photo;
import ru.kazachkov.florist.api.model.Stock;
import ru.kazachkov.florist.api.model.request.AddUpdateGoodItemAuthJS;
import ru.kazachkov.florist.data.DataSource;
import ru.kazachkov.florist.newgoods.NewGoodsContract;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class NewGoodsPresenter implements NewGoodsContract.Presenter {
    private final NewGoodsActivity context;
    private final DataSource dataSource;
    private final String sessionId;
    private final String storageId;

    private final CompositeSubscription subscriptions;


    public NewGoodsPresenter(NewGoodsActivity context, DataSource dataSource, String sessionId, String storageId) {
        this.context = context;
        this.dataSource = dataSource;
        this.sessionId = sessionId;
        this.storageId = storageId;
        subscriptions = new CompositeSubscription();
    }

    @Setter
    @Getter
    private String goodsId;

    private GoodsModel oldGoodsModel;
    private boolean update;
    @Setter
    private NewGoodsContract.View view;


    @Getter
    private ObservableBoolean ready = new ObservableBoolean();
    @Getter
    private ObservableBoolean locked = new ObservableBoolean();
    @Getter
    private ObservableBoolean sendInProgress = new ObservableBoolean();
    @Setter
    @Getter
    private String newPhotoPath;
    private List<String> photosUri = new ArrayList<>();
    @Getter
    @Setter
    private int addCount;

    @Getter
    private List<EdIzm> units = new ArrayList<>();
    @Getter
    private List<Category> categories = new ObservableArrayList<>();


    @Getter
    private AddUpdateGoodItemAuthJS.Data data = new AddUpdateGoodItemAuthJS.Data();
    @Getter
    private ObservableField<String> barcode = new ObservableField<>();
    @Getter
    private ObservableField<String> name = new ObservableField<>();
    @Getter
    private ObservableInt groupPosition = new ObservableInt();
    @Getter
    private ObservableInt edIzmPosition = new ObservableInt();
    @Getter
    private Category currentCategory;
    @Getter
    private EdIzm currentUnit;

    public void setScanResult(String code) {
        barcode.set(code);
    }

    public void setBarcode(String code) {
        data.setItemBarCode(code);
    }

    public void bingGoodsModel(GoodsModel goodsModel) {
        update = true;
        oldGoodsModel = goodsModel;

        data.setItem1CId(goodsModel.getItem().getFlower1CId());

        data.setEdIzmId(goodsModel.getEdIzm().getEdIzm1CId());
        setEdIzmPos();

        data.setGroup1CId(goodsModel.getCategories().get(0).getCategory1CId());
        setGroupPos();

        data.setItemBarCode(goodsModel.getItem().getFlowerArticle());
        barcode.set(goodsModel.getItem().getFlowerArticle());

        data.setItem1CName(goodsModel.getItem().getFlowerName());
        name.set(goodsModel.getItem().getFlowerName());

        data.setIsZeroStocked(goodsModel.getItem().getIsZeroStock());
        locked.set(goodsModel.getItem().getIsLockEd() == 1);

        List<Photo> photoList = oldGoodsModel.getPhoto();

        for (Photo item : photoList) {
            this.photosUri.add(item.getPath());
        }
        updateShowPhotos(photosUri);
    }

    public void setName(String name) {
        if (!TextUtils.isEmpty(name)) {
            ready.set(true);
            data.setItem1CName(name);
        } else {
            ready.set(false);
        }
    }

    public void onSend() {
        data.setSessionId(sessionId);
        data.setStorage1CId(storageId);

        if (!equals(data, oldGoodsModel) || photoChanged(oldGoodsModel, photosUri)) {
            context.sendGoods(data);
        } else {
            context.finishActivity();
        }
    }

    private boolean photoChanged(GoodsModel goodsModel, List<String> photosPath) {
        return goodsModel.getPhoto().size() != photosPath.size();
    }

    private boolean equals(AddUpdateGoodItemAuthJS.Data data, GoodsModel goodsModel) {
        if (data == null || goodsModel == null) return false;
        if (!data.getEdIzmId().equals(goodsModel.getEdIzm().getEdIzm1CId())) return false;
        if (!data.getGroup1CId().equals(goodsModel.getCategories().get(0).getCategory1CId()))
            return false;
        if (!data.getItem1CName().equals(goodsModel.getName())) return false;
        if (!data.getItemBarCode().equals(goodsModel.getItem().getFlowerArticle())) return false;
        return true;
    }


    public void setUnit(int i, long l) {
        data.setEdIzmId(units.get(i).getEdIzm1CId());
        currentUnit = units.get(i);
    }

    public void setGroup(int i, long l) {
        data.setGroup1CId(categories.get(i).getCategory1CId());
        currentCategory = categories.get(i);
    }


    private void setEdIzmPos() {
        if (units != null && units.size() > 0 && !TextUtils.isEmpty(data.getEdIzmId())) {
            int pos = 0;
            for (EdIzm edizm : units) {
                if (edizm.getEdIzm1CId().equals(data.getEdIzmId())) {
                    edIzmPosition.set(pos);
                }
                pos++;
            }
        }
    }

    private void setGroupPos() {
        if (categories != null && categories.size() > 0 && !TextUtils.isEmpty(data.getGroup1CId())) {
            int pos = 0;
            for (Category category : categories) {
                if (category.getCategory1CId().equals(data.getGroup1CId())) {
                    groupPosition.set(pos);
                }
                pos++;
            }
        }
    }


    @Override
    public void onPhotoSuccessfulTake() {
        photosUri.add(newPhotoPath);
        updateShowPhotos(photosUri);
    }

    @Override
    public List<String> getPhotos() {
        return photosUri;
    }

    private void updateShowPhotos(List<String> photosUri) {
        if (photosUri.isEmpty()) {
            view.showAddPhotoButton();
        } else {
            view.showPhotos(photosUri);
        }
    }

    @Override
    public Observable<String> getPhotoObs() {
        return Observable.from(photosUri);
    }

    public Stock getStock(String id) {
        if (update) return oldGoodsModel.getStock();
        return new Stock(storageId, id, 0, -1);
    }


    @Override
    public void subscribe() {
        Subscription subscriptionUnits = dataSource.getUnits()
                .flatMap(edIzms -> Observable.from(edIzms).toSortedList((edIzm, edIzm2) -> edIzm.getEdIzmName().compareTo(edIzm2.getEdIzmName())))
                .subscribe(companyAtGlanceDT -> {
                    units.addAll(companyAtGlanceDT);
                    view.showUnits(units);
                    setEdIzmPos();
                }, throwable -> {
                    view.showError();
                });

        Subscription subscriptionCategories = dataSource.getGroupsObs()
                .subscribe(userObservableList1 -> {
                    categories.addAll(userObservableList1);
                    view.showCategories(categories);
                    setGroupPos();
                }, throwable -> {
                    view.showError();
                });

        subscriptions.add(subscriptionUnits);
        subscriptions.add(subscriptionCategories);
    }

    @Override
    public void unsubscribe() {
        subscriptions.clear();
    }

    @Override
    public void onPhotoClick(int position) {
        view.showScreenGallery(photosUri, position);
    }
}
