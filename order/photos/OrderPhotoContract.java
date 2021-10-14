package ru.kazachkov.florist.order.photos;

import java.util.List;

import ru.kazachkov.florist.interfaces.BasePresenter;
import ru.kazachkov.florist.interfaces.BaseView;

/**
 * Created by ishmukhametov on 16.11.16.
 */

public interface OrderPhotoContract {

    interface View extends BaseView<Presenter> {
        void showPhotos(List<String> photos);

        void showError(Throwable throwable);

        void showGalleryScreen(List<String> newPhotosPath, int position);
    }

    interface Presenter extends BasePresenter {

        void handlePhotoPath(String path);

        void onPhotoSuccessfulTake();

        void onOpenClick(int position);

        List<String> getNewPhotos();
    }
}
