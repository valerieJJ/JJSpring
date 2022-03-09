package valerie.myservices;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.apache.http.HttpRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.bson.json.JsonObject;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import valerie.myModel.User;
import valerie.myModel.requests.FavoriteRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;

@Aspect
@Component
@Order(1)
public class PermissionAspect {
    //定义一个AOP切面类时，在类上加个@Aspect注解，然后用@Component注解将该类交给SPring管理
    @Pointcut("@annotation(valerie.myModel.PermissionAnnotation)")
    private void permissionCheck(){
    }

    @Around("permissionCheck()")
    public Object permissionChecking(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] objects = joinPoint.getArgs();//获取接口类的请求参数


        System.out.println("user permission checking...");
        String mid = (String) objects[0];

        HttpSession session = ((HttpServletRequest) objects[2]).getSession();
        User user = (User) session.getAttribute("user");

        System.out.println("target=" + joinPoint.getTarget());

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();//获取request
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();//获取response


        try {
            if(user.getUsername().equals("michael"))
                throw new Exception("no privilege");
        } catch (Exception ex) {
//            request.setAttribute("msg", "no privilege");
            System.out.println("no privilege");
            response.sendRedirect(request.getContextPath() + "/user/dologin");

//            try {
//                request.getRequestDispatcher("/index.html").forward(
//                        request, response);
//            } catch (ServletException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
//        if(user==null || user.getUid()<0 || mid<0){
//            return "{\"message\":\"illegal id\",\"code\":403}";
//        }
        return joinPoint.proceed();

    }

}
