package ru.kazachkov.florist.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;

@Data
public class WtdAction implements Parcelable {
    int actionId;
    String actionName;
    int actionSequence;
    int isDefault;

    protected WtdAction(Parcel in) {
        actionId = in.readInt();
        actionName = in.readString();
        actionSequence = in.readInt();
        isDefault = in.readInt();
    }

    public static final Creator<WtdAction> CREATOR = new Creator<WtdAction>() {
        @Override
        public WtdAction createFromParcel(Parcel in) {
            return new WtdAction(in);
        }

        @Override
        public WtdAction[] newArray(int size) {
            return new WtdAction[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(actionId);
        parcel.writeString(actionName);
        parcel.writeInt(actionSequence);
        parcel.writeInt(isDefault);
    }
}
