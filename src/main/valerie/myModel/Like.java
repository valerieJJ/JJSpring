package valerie.myModel;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class Like {

    @JsonIgnore
    private String _id;

    private int uid;

    private int mid;

//    private String label;

    private long timestamp;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Like() {
    }

    public Like(int uid, int mid) {
        this.uid = uid;
        this.mid = mid;
        this.timestamp = new Date().getTime();
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

//    public String getLabel() {
//        return label;
//    }
//
//    public void setLabel(String label) {
//        this.label = label;
//    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
