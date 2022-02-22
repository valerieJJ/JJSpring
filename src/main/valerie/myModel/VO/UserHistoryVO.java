package valerie.myModel.VO;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.HashMap;

public class UserHistoryVO implements Serializable {

    private int uid;
    private String username;
    private HashMap<Integer, Double> myMovieScores;//<mid, userScore>
    private HashMap<Integer, String> myMovieTags;//<mid, usertag>

    public UserHistoryVO() {
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public HashMap<Integer, Double> getMyMovieScores() {
        return myMovieScores;
    }

    public void setMyMovieScores(HashMap<Integer, Double> myMovieScores) {
        this.myMovieScores = myMovieScores;
    }

    public HashMap<Integer, String> getMyMovieTags() {
        return myMovieTags;
    }

    public void setMyMovieTags(HashMap<Integer, String> myMovieTags) {
        this.myMovieTags = myMovieTags;
    }


}
