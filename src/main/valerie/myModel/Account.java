package valerie.myModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Account extends User implements Serializable {

    @JsonIgnore
    private String _id;

    private  String nickname = "mike";

    private int uid;

    private String username;

    private String password;

    private boolean first;

    private long timestamp;

    private List<String> prefGenres = new ArrayList<>();
//
//    public Account(String username, String password, Collection<? extends GrantedAuthority> authorities) {
//        super(username, password, authorities);
//    }
//
//    public Account(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
//        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
//    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.uid = username.hashCode();
        this.username = username;
    }

    public String getNickname(){return nickname;}

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean passwordMatch(String password) {
        return this.password.compareTo(password) == 0;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public List<String> getPrefGenres() {
        return prefGenres;
    }

    public void setPrefGenres(List<String> prefGenres) {
        this.prefGenres = prefGenres;
    }
}
