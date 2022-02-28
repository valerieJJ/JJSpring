package valerie.myservices;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.bson.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;


@Aspect
@Component
public class MyAspect {
    private Logger logger =  LoggerFactory.getLogger(MyAspect.class);
    //切入点：待增强的方法是RecMovieController类中的所有方法
//    @Pointcut("@args(valerie.mycontrollers.MovieController)")
    //execution（<修饰符模式>？<返回类型模式><方法名模式>（<参数模式>）<异常模式>？）除了返回类型模式，方法名模式和参数模式外，其它项都是可选的。
    @Pointcut("execution(public * valerie.mycontrollers.RecMovieController.*(..))")
    public void point(){
        System.out.println("\nHere comes the pointCut...");
    }
    @Pointcut("execution(public * valerie.mycontrollers.UserController.doFavorite())")
    public void permissionCheckPoint(){
        System.out.print("\n favor-point...");
    }

//    @Around("permissionCheckPoint()")
    public Object permissionCheck(ProceedingJoinPoint joinPoint){
        System.out.print("\n permissionChecking...");
        Object[] objects = joinPoint.getArgs();//获取请求参数
//        Long id = ((JsonObject) objects[0]).get
        return  null;
    }

    //前置通知
    @Before("point()")
    public void deBefore(JoinPoint jp) throws Throwable {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 记录下请求内容
        System.out.println("do favor aspect\nURL : " + request.getRequestURL().toString());
    }



    //返回通知
    @AfterReturning(returning = "ret", pointcut = "point()")
    public void doAfterReturning(Object ret) throws Throwable {
        // 处理完请求，返回内容
        System.out.println("aop : login to explore more");
    }




    //异常通知
//    @AfterThrowing(throwing = "ex", pointcut = "point()")
    public void throwss(JoinPoint jp,Exception ex){
        System.out.println("异常通知：方法异常时执行.....");
        System.out.println("产生异常的方法："+jp);
        System.out.println("异常种类："+ex);
    }

    //后置通知
//    @After("point()")
    public void after(JoinPoint jp){
        System.out.println("后置通知：最后且一定执行.....");
    }
}
