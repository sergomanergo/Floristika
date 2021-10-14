package ru.kazachkov.florist.adapters;


import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import ru.kazachkov.florist.R;
import ru.kazachkov.florist.api.model.Photo;

public class ImagePagerAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    ObservableArrayList<Photo> images;

    View.OnClickListener onClickListener;

    ObservableList.OnListChangedCallback<ObservableList<Photo>> onListChangedCallback =
            new ObservableList.OnListChangedCallback<ObservableList<Photo>>() {
                @Override
                public void onChanged(ObservableList<Photo> vms) {
                    notifyDataSetChanged();
                }

                @Override
                public void onItemRangeChanged(ObservableList<Photo> vms, int i, int i1) {
                    notifyDataSetChanged();
                }

                @Override
                public void onItemRangeInserted(ObservableList<Photo> vms, int i, int i1) {
                    notifyDataSetChanged();
                }

                @Override
                public void onItemRangeMoved(ObservableList<Photo> vms, int i, int i1, int i2) {
                    notifyDataSetChanged();
                }

                @Override
                public void onItemRangeRemoved(ObservableList<Photo> vms, int i, int i1) {
                    notifyDataSetChanged();
                }
            };

    public ImagePagerAdapter(Context context, ObservableArrayList<Photo> images, View.OnClickListener onClickListener) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.images = images;
        this.onClickListener = onClickListener;
        this.images.addOnListChangedCallback(onListChangedCallback);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.item_slide_photo, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        imageView.setClickable(true);
        imageView.setOnClickListener(onClickListener);
        Picasso.with(mContext)
                .load(images.get(position).getFile())
                .into(imageView);
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
