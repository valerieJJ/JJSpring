package valerie.myUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService; //需要在service的'@Service("userDetailsService")'中指明注入userDetailService

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(password());
    }

    @Bean
    PasswordEncoder password(){ return new BCryptPasswordEncoder(); }

    @Override
    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().antMatchers("/static/assets/**");
//        web.ignoring().antMatchers("/static/assets/img/*");
//        web.ignoring().antMatchers("/static/assets/img/**");
//        web.ignoring().antMatchers("/static/css/*");
//        web.ignoring().antMatchers("/css/*");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage("/index.html") // 登陆页面设置
//                .loginProcessingUrl("/user/dologin") // 登陆访问路径
                .defaultSuccessUrl("/user/goindex").permitAll() // 登陆成功之后的跳转路径
                .and().authorizeRequests()
                .antMatchers("/user/dologin", "/user/doregister" // 设置可以直接访问的路径，不需要认证
                        , "/user/register"
//                        ,"/user/account","/user/goindex"
                ).permitAll()
                .anyRequest().authenticated()
                .and().csrf().disable(); // 关闭csrf防护
    }
}
