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
import valerie.myModel.User;
import valerie.myModel.requests.FavoriteRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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

        int mid = (int)objects[0];

        HttpSession session = ((HttpServletRequest) objects[2]).getSession();
        User user = (User) session.getAttribute("user");

        System.out.println("user identity permission checking...");
        if(user==null || user.getUid()<0 || mid<0){
            return "{\"message\":\"illegal id\",\"code\":403}";
        }
        return joinPoint.proceed();

    }

}
