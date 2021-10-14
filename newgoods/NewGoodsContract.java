package ru.kazachkov.florist.newgoods;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.text.TextWatcher;
import android.widget.AdapterView;

import java.util.List;

import ru.kazachkov.florist.api.model.Category;
import ru.kazachkov.florist.api.model.EdIzm;
import ru.kazachkov.florist.interfaces.BasePresenter;
import ru.kazachkov.florist.interfaces.BaseView;
import ru.kazachkov.florist.interfaces.PhotoContainer;

/**
 * Created by ishmukhametov on 16.09.16.
 */

public interface NewGoodsContract {

    interface Presenter extends BasePresenter, PhotoContainer {
        List<EdIzm> getUnits();

        List<Category> getCategories();

        void onPhotoClick(int position);
    }

    interface View extends BaseView<Presenter> {

        void showAddPhotoButton();

        void showPhotos(List<String> photosUri);

        void showError();

        void showUnits(List<EdIzm> units);

        void showCategories(List<Category> categories);

        void showScreenGallery(List<String> photosUri, int position);
    }

    interface ViewModel {
        void onAddPhotoClick();

        void showAddPhotoButton();

        void showPhotos();

        void onScanClick();

        TextWatcher getNameListener();

        TextWatcher getBarcodeListener();

        AdapterView.OnItemSelectedListener getUnitListener();

        AdapterView.OnItemSelectedListener getGroupListener();

        ObservableBoolean getReady();

        void onSendClick();

        void onAddClick();

        ObservableField<String> getBarcode();

        ObservableField<String> getName();

        ObservableInt getGroupPos();

        ObservableInt getUnitPos();

        ObservableBoolean getLocked();

        ObservableBoolean getShowPhotos();

        boolean isWithAddButton();

    }
}
