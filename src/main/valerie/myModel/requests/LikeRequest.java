package valerie.myModel.requests;

public class LikeRequest {

    private int uid;
    private int mid;

    public void setUid(int uid) {
        this.uid = uid;
    }

    public LikeRequest(int uid, int mid) {
        this.uid = uid;
        this.mid = mid;
    }

    public int getMid() {
        return mid;
    }

    public int getUid() {
        return uid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

}
