package ru.kazachkov.florist.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.kazachkov.florist.api.realm.RealmFastAction;
import ru.kazachkov.florist.interfaces.ToRealmConvert;

@AllArgsConstructor
public class FastAction implements Parcelable, ToRealmConvert {
    @SerializedName("faID")
    @Getter
    @Setter
    private int id;

    @SerializedName("faName")
    @Getter
    @Setter
    private String name;


    public FastAction() {
    }

    public FastAction(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public static final Creator<FastAction> CREATOR = new Creator<FastAction>() {
        @Override
        public FastAction createFromParcel(Parcel in) {
            return new FastAction(in);
        }

        @Override
        public FastAction[] newArray(int size) {
            return new FastAction[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public RealmFastAction convertToFunc() {
        return RealmFastAction.newInstance(id, name);
    }

}
