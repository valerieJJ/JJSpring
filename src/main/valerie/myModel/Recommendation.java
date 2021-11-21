package valerie.myModel;

public class Recommendation {
    private int mid;
    private double score;

    public Recommendation(){
    }
    public Recommendation(int mid, double score){
        this.mid = mid;
        this.score = score;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }


}
