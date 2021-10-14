package ru.kazachkov.florist.interfaces;


import java.util.List;

import rx.Observable;

public interface PhotoContainer {
    void onPhotoSuccessfulTake();

    List<String> getPhotos();

    Observable<String> getPhotoObs();

    void setNewPhotoPath(String path);
}
