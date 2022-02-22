package valerie.myModel;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Date;

public class Comment implements Serializable {

    @JsonIgnore
    private String _id;

    private int uid;

    private int mid;

    private String content;

    private long timestamp;

    public String get_id() {
        return _id;
    }

    public Comment() {
    }

    public Comment(int uid, int mid, String content) {
        this.uid = uid;
        this.mid = mid;
        this.content = content;
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

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
