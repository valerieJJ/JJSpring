package valerie.myModel.requests;

public class TagRequest {
    private int uid;
    private int mid;
    private String tag;

    public int getUid() {
        return uid;
    }

    public TagRequest() {
    }

    public TagRequest(int uid, int mid, String tag) {
        this.uid = uid;
        this.mid = mid;
        this.tag = tag;
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

}
