package valerie.mycontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
//import valerie.myDAO.UserServiceImpl;
import valerie.myModel.User;
import valerie.myservices.MongodbService;
import valerie.myservices.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class LoginIntercepter implements HandlerInterceptor {
    @Autowired
    private UserService userService;
//    @Autowired
//    private UserServiceImpl userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("This is LoginIntercepter ......");
        Cookie[] cookies = request.getCookies();
        // 如果没有cookie，重定向到登录洁面
        if(null==cookies){
            response.sendRedirect(request.getContextPath() + "/user/dologin");
            return false;
        }
        String userticket = null;
        for(Cookie item: cookies){
            if(item.getName().equals("userticket")){
                userticket = item.getValue();
                break;
            }
        }

        // 如果cookie里面没有用户登录信息，重定向到登陆界面
        if(!StringUtils.hasText(userticket)){
            response.sendRedirect(request.getContextPath() + "/user/dologin");
            return  false;
        }

        // 获取登录后保存在session中的用户信息，如果为null，则说明session已过期
        HttpSession session = request.getSession();
        Object object = (User) session.getAttribute("user");
        if(object==null){
            User user = userService.findByUsername(userticket);
            session.setAttribute("user", user);
        }
        return true;
    }
}
