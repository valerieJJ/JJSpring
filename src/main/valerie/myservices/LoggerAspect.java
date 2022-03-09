package valerie.myservices;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Aspect
@Component
public class LoggerAspect {

    private Logger logger =  LoggerFactory.getLogger(LoggerAspect.class);

//    RecService recService = SpringUtil.getBean(RecService.class);


    // 切入点：待增强的方法是RecMovieController类中的所有方法
    // @Pointcut("@args(valerie.mycontrollers.MovieController)")
    // execution（<修饰符模式>？<返回类型模式><方法名模式>（<参数模式>）<异常模式>？）除了返回类型模式，方法名模式和参数模式外，其它项都是可选的。
    @Pointcut("execution(public * valerie.mycontrollers.RecMovieController.*(..))")
    public void point(){
        System.out.println("\nHere comes the pointCut...");
    }

    //前置通知
    @Before("point()")
    public void deBefore(JoinPoint jp) throws Throwable {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();
//        Cookie[] cookies = request.getCookies();

//        HandlerMethod h
//        Class<?> clazz =

//        if(cookies==null){
//            response.sendRedirect(request.getContextPath()+"/login");
//        }else{
//            for (Cookie cookie:cookies){
//                if("")
//            }
//        }

//        HotMovieRequest hotMovieRequest = new HotMovieRequest(6);//取出6个
//        CompletableFuture<List<MovieVO>> hotMovieVOS = recService.getHotRecommendations(hotMovieRequest);
//        LatestMovieRequest latestMovieRequest = new LatestMovieRequest(6);//取出6个
//        CompletableFuture<List<MovieVO>> latestMovieVOS = recService.getLatestRecommendations(latestMovieRequest);
//
//        CompletableFuture.allOf(hotMovieVOS, latestMovieVOS).join();
//        List<MovieVO> hotmovies = hotMovieVOS.get();
//        List<MovieVO> latestmovies = hotMovieVOS.get();

        // 记录下请求内容
        logger.info("do favor aspect\nURL : " + request.getRequestURL().toString());
//        System.out.println();
//        System.out.println("MyAspect : " + hotmovies.size());
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
