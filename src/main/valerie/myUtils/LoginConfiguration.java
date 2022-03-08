package valerie.myUtils;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import valerie.mycontrollers.LoginIntercepter;

@Configuration
public class LoginConfiguration implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration ir = registry.addInterceptor(new LoginIntercepter());
        ir.addPathPatterns("/user/**");
        ir.excludePathPatterns("/user/dologin","/user/login", "/user/doregister", "/user/goindex", "/user/index","/user/register","/index");

    }
}
