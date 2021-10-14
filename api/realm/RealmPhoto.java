package ru.kazachkov.florist.api.realm;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ru.kazachkov.florist.api.model.Photo;
import ru.kazachkov.florist.interfaces.FromRealmConvert;

public class RealmPhoto extends RealmObject implements FromRealmConvert<Photo> {
    @PrimaryKey
    private String id;
    private String itemId;
    private String path;

    public RealmPhoto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public Photo fromRealm() {
        return new Photo(itemId, path);
    }
}
