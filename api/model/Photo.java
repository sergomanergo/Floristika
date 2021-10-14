package ru.kazachkov.florist.api.model;


import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.kazachkov.florist.api.realm.RealmPhoto;
import ru.kazachkov.florist.interfaces.ToRealmConvert;

@Data
@AllArgsConstructor
public class Photo implements ToRealmConvert, Parcelable {
    private final String itemId;
    private final String path;

    protected Photo(Parcel in) {
        itemId = in.readString();
        path = in.readString();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    @Override
    public RealmPhoto convertToFunc() {
        RealmPhoto photo = new RealmPhoto();
        photo.setId(UUID.randomUUID().toString());
        photo.setItemId(itemId);
        photo.setPath(path);
        return photo;
    }

    public Uri getFile() {
        return Uri.fromFile(new File(path));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(itemId);
        parcel.writeString(path);
    }
}
