package valerie.myModel.DTO;

import valerie.myModel.Movie;

import java.io.Serializable;

public class MovieDTO implements Serializable {

    private int mid;

    private int count;

    public MovieDTO(int mid) {
        this.mid = mid;
    }

    public MovieDTO(int mid, int count) {
        this.mid = mid;
        this.count = count;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public MovieDTO() {
    }

}
