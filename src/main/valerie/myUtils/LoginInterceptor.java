package valerie.myUtils;

import io.netty.util.internal.StringUtil;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import valerie.myModel.User;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class LoginInterceptor implements HandlerInterceptor {

//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        HttpSession session = request.getSession();
//        System.out.println("sessionId为：" + session.getId());
//
//        // 获取用户信息，如果没有用户信息直接返回提示信息
//        Object userInfo = session.getAttribute("userInfo");
//        if (userInfo == null) {
//            System.out.println(" please login first ");
//            response.getWriter().write("Please Login In");
//            return false;
//        } else {
//            System.out.println("you have already logged in");
//            User user = (User) session.getAttribute("user");
//            System.out.println("account info: "+user.getUsername());
//        }
//        return true;
//    }


    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String reqPath = httpServletRequest.getServletPath();
        // 只有返回true才会继续向下执行，返回false取消当前请求
        HttpSession session = httpServletRequest.getSession();
        if (session == null || session.getAttribute("user")==null) {
            System.out.println("preHandler: access#{"+session.getId()+"}, not logged in, return"+reqPath);
            System.out.println("HttpStatus:"+HttpStatus.UNAUTHORIZED.value());
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
//            httpServletResponse.sendRedirect("/user/index");
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {

    }
}

