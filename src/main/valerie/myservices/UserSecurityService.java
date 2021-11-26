package valerie.myservices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import valerie.myModel.User;

import java.util.List;

@Service("userDetailsService")
public class UserSecurityService implements UserDetailsService {

    @Autowired
    private UserService userService;

    private List getAuthority(String authority){
        List<GrantedAuthority> auths = AuthorityUtils.commaSeparatedStringToAuthorityList(authority);
        return auths;
    }
    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        User user = userService.loginUser(name);
        if(user==null){
            //数据库没有用户名，认证失败
            throw new UsernameNotFoundException("User does not exist");
        }

        List<GrantedAuthority> auths = getAuthority("lover");

        return new org.springframework.security.core.userdetails.User(user.getUsername()
                            , new BCryptPasswordEncoder().encode(user.getPassword())
                            , auths);
    }
}
