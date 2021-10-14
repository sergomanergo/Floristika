package ru.kazachkov.florist.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import ru.kazachkov.florist.R;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by ishmukhametov on 05.12.16.
 */

public class PhotosPagerAdapter extends PagerAdapter {

    private List<String> photos;
    private LayoutInflater inflater;

    public PhotosPagerAdapter(Context context, List<String> photos) {
        this.photos = photos;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return photos == null ? 0 : photos.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.list_item_photo_view, container, false);

        PhotoView photoView = (PhotoView) view.findViewById(R.id.photo_view);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);

        Uri uri = Uri.fromFile(new File(photos.get(position)));
        final PhotoViewAttacher[] attacher = {null};

        Callback imageLoadedCallback = new Callback() {

            @Override
            public void onSuccess() {
                if (attacher[0] != null) {
                    attacher[0].update();
                } else {
                    attacher[0] = new PhotoViewAttacher(photoView);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                progressBar.setVisibility(View.GONE);
            }
        };

        progressBar.setVisibility(View.VISIBLE);
        Picasso.with(view.getContext())
                .load(uri)
                .into(photoView, imageLoadedCallback);


        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
