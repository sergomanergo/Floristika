package ru.kazachkov.florist.order.photos;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import ru.kazachkov.florist.api.model.Photo;
import ru.kazachkov.florist.data.DataSource;
import ru.kazachkov.florist.interfaces.OrderData;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ishmukhametov on 16.11.16.
 */

public class OrderPhotoPresenter implements OrderPhotoContract.Presenter {

    private final DataSource dataSource;
    private final String orderId;
    private String currentPhotoPath;
    private List<String> newPhotosPath;
    private List<String> oldPhotosPath;
    private OrderPhotoContract.View view;

    CompositeSubscription compositeSubscription = new CompositeSubscription();

    public OrderPhotoPresenter(OrderPhotoContract.View view, DataSource dataSource, @Nullable OrderData orderData) {
        this.view = view;
        this.newPhotosPath = new ArrayList<>();
        this.oldPhotosPath = new ArrayList<>();
        this.dataSource = dataSource;
        if (orderData != null) {
            this.orderId = orderData.getOrder1CId();
        } else {
            this.orderId = null;
        }
    }

    @Override
    public void handlePhotoPath(String path) {
        currentPhotoPath = path;
    }

    @Override
    public void onPhotoSuccessfulTake() {
        if (currentPhotoPath == null)
            throw new NullPointerException("Photo path can't be null, you call handlePhotoPath()?");
        newPhotosPath.add(currentPhotoPath);
        currentPhotoPath = null;

        List<String> list = new ArrayList<>(oldPhotosPath);
        list.addAll(newPhotosPath);
        view.showPhotos(list);
    }

    @Override
    public void onOpenClick(int position) {
        view.showGalleryScreen(newPhotosPath, position);
    }

    @Override
    public List<String> getNewPhotos() {
        return newPhotosPath;
    }

    @Override
    public void subscribe() {
        if (TextUtils.isEmpty(orderId)) return;

        Subscription subscription = dataSource.getOrderPhotos(orderId)
                .flatMap(photos -> Observable.from(photos).map(Photo::getPath).toList())
                .first()
                .subscribe(strings -> {
                    oldPhotosPath = strings;

                    List<String> list = new ArrayList<>(oldPhotosPath);
                    list.addAll(newPhotosPath);
                    view.showPhotos(list);
                }, throwable -> {
                    view.showError(throwable);
                });

        compositeSubscription.add(subscription);
    }

    @Override
    public void unsubscribe() {
        compositeSubscription.clear();
    }
}
