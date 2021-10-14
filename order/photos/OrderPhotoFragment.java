package ru.kazachkov.florist.order.photos;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.kazachkov.florist.GalleryActivity;
import ru.kazachkov.florist.R;
import ru.kazachkov.florist.databinding.ItemPhotoV2Binding;
import ru.kazachkov.florist.tools.Utils;

public class OrderPhotoFragment extends Fragment implements OrderPhotoContract.View {


    private OrderPhotoContract.Presenter presenter;
    private OrderPhotoAdapter adapter;
    private TextView textView;

    public static OrderPhotoFragment newInstance() {

        Bundle args = new Bundle();

        OrderPhotoFragment fragment = new OrderPhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_photo, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        textView = (TextView) view.findViewById(R.id.photo_count_text_view);
        adapter = new OrderPhotoAdapter(new ArrayList<>(0), presenter);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.subscribe();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.unsubscribe();
    }

    @Override
    public void showError(Throwable throwable) {
        Utils.toast(getContext(), throwable.getMessage());
    }

    @Override
    public void showPhotos(List<String> photos) {
        adapter.setPhotos(photos);
        textView.setText(getString(R.string.photo_count, photos.size()));
    }

    @Override
    public void showGalleryScreen(List<String> newPhotosPath, int position) {
        GalleryActivity.start(getContext(), position, (ArrayList<String>) newPhotosPath);
    }

    @Override
    public void setPresenter(OrderPhotoContract.Presenter presenter) {
        this.presenter = presenter;
    }


    private class OrderPhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        List<String> photosPath;
        OrderPhotoContract.Presenter actionHandler;

        OrderPhotoAdapter(List<String> photos, OrderPhotoContract.Presenter presenter) {
            actionHandler = presenter;
            setPhotos(photos);
        }

        private void setPhotos(List<String> photosPath) {
            this.photosPath = photosPath;
            notifyDataSetChanged();
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new PhotoHolder(inflater.inflate(R.layout.item_photo_v2, parent, false));
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            holder.binding.setPosition(position);
            holder.binding.setVm(photosPath.get(position));
            holder.binding.setActionHandler(presenter);
        }

        @Override
        public int getItemCount() {
            return photosPath != null ? photosPath.size() : 0;
        }
    }


    private class PhotoHolder extends RecyclerView.ViewHolder {
        private final ItemPhotoV2Binding binding;

        PhotoHolder(View itemView) {
            super(itemView);
            this.binding = DataBindingUtil.bind(itemView);
        }
    }
}
