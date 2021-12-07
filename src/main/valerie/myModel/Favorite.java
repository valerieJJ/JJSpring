package valerie.myModel;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Date;

public class Favorite implements Serializable {

    @JsonIgnore
    private String _id;

    private int uid;

    private int mid;

    private long timestamp;

    public Favorite() {
    }

    public String get_id() {
        return _id;
    }

    public int getMid() {
        return mid;
    }

    public Favorite(int uid, int mid) {
        this.uid = uid;
        this.mid = mid;
        this.timestamp = new Date().getTime();
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
