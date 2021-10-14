package ru.kazachkov.florist.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Collection;
import java.util.List;

import io.realm.RealmObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.kazachkov.florist.api.model.response.OrdersListAction;
import ru.kazachkov.florist.api.realm.RealmSalePnt;
import ru.kazachkov.florist.interfaces.SpinnerText;
import ru.kazachkov.florist.interfaces.ToRealmConvert;

@AllArgsConstructor
public @Data
class UserAuthRole {

    private UserSessionRole userRoleData;
    private TotalUpdateData totalUpdateData;

    @Data
    public static class UserSessionRole {
        private String roleName;
        private int roleId;
        private String sessionId;
        private List<SalePnt> salePnts;
        @SerializedName("isDemo")
        private int demo;

        public boolean isDemo() {
            return demo == 1;
        }
    }

    @Data
    public static class SalePnt implements ToRealmConvert, Parcelable, SpinnerText {
        private String storage1CId;
        private int isManager;
        private String storageName;

        protected SalePnt(Parcel in) {
            storage1CId = in.readString();
            isManager = in.readInt();
            storageName = in.readString();
        }

        public SalePnt() {
        }

        public static final Creator<SalePnt> CREATOR = new Creator<SalePnt>() {
            @Override
            public SalePnt createFromParcel(Parcel in) {
                return new SalePnt(in);
            }

            @Override
            public SalePnt[] newArray(int size) {
                return new SalePnt[size];
            }
        };

        @Override
        public RealmObject convertToFunc() {
            RealmSalePnt realmSalePnt = new RealmSalePnt();
            realmSalePnt.setIsManager(isManager);
            realmSalePnt.setStorage1CId(storage1CId);
            realmSalePnt.setStorageName(storageName);
            return realmSalePnt;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(storage1CId);
            parcel.writeInt(isManager);
            parcel.writeString(storageName);
        }

        @Override
        public String getSpinnerText() {
            return storageName;
        }

        @Override
        public String toString() {
            return storageName;
        }
    }

    @Data
    public static class TotalUpdateData {
        @SerializedName("TotalItemsData")
        private FastUpdate fastUpdateData;

        @SerializedName("ordersListActions")
        private List<OrdersListAction> ordersListActions;

        @SerializedName("ordersFastActions")
        private List<OrderFastActions> ordersFastActions;

        public Collection<? extends ToRealmConvert> toRealmConverts() {
            Collection<ToRealmConvert> converts = fastUpdateData.toRealmConverts();
            if (ordersListActions != null) converts.addAll(ordersListActions);
            if (ordersFastActions != null) converts.addAll(ordersFastActions);
            return converts;
        }
    }
}

