package valerie.myModel;

public class Song {
    private String name;
    private String language;
    private int likes;

    public Song(){}

    public Song(String name, String language, int likes) {
        this.name = name;
        this.language = language;
        this.likes = likes;
    }

    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }

    public String getName(){
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getLikes() {
        return likes;
    }
    public void setLikes(int likes) {
        this.likes = likes;
    }
}
