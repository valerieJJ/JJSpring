package valerie.mycontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import valerie.myModel.Favorite;
import valerie.myModel.Movie;
import valerie.myModel.User;
import valerie.myModel.VO.MovieVO;
import valerie.myModel.requests.HotMovieRequest;
import valerie.myModel.requests.LatestMovieRequest;
import valerie.myModel.requests.LoginUserRequest;
import valerie.myModel.requests.RegisterUserRequest;
import valerie.myservices.FavoriteService;
import valerie.myservices.MovieService;
import valerie.myservices.RecService;
import valerie.myservices.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private RecService recService;
    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private MovieService movieService;

    /****************************  Register  **************************/
    @RequestMapping("/user/register")
    public String register(){
        return "register";
    }
    @RequestMapping("/user/doregister")
    public String register(Model model, HttpServletRequest request) throws ExecutionException, InterruptedException {
        String name = request.getParameter("username");
        String password = request.getParameter("password");

        System.out.println("username is " + name);
        System.out.println("password is " + password);

        if(userService.checkUserExist(name)){
            System.out.println("user already exists, please login");
            model.addAttribute("success",false);
            model.addAttribute("message"," ???????????????????????????");
            return "register";
        }else{
            model.addAttribute("success", true);
            User user = userService.registerUser(new RegisterUserRequest(name,password));
            model.addAttribute("user", user);
            getRecs2(model);
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            System.out.println("register successful!");
        }
        return "mainIndex";
    }


    @RequestMapping(value = "/user/dologin", method = {RequestMethod.GET,RequestMethod.POST})
    public String login(@ModelAttribute("user") User usr, @ModelAttribute("movie") Movie movieReq
            , Model model, HttpServletRequest request, HttpServletResponse response) throws UnknownHostException, ExecutionException, InterruptedException {

        LoginUserRequest loginUserRequest = new LoginUserRequest(usr.getUsername(),usr.getPassword());

        HttpSession session = request.getSession();
        User userCheck = (User) session.getAttribute("user");
        if(userCheck!=null){
            System.out.println("?????????");
            return "redirect:main";
        }

        User user = userService.loginUser(loginUserRequest,request,response);

        if(user==null){
            System.out.println("Account does not exist");
            return "login";
        }else {
            System.out.println("\nGet username="+user.getUsername());
            System.out.println("Get password="+user.getPassword());

            // ??????????????????????????????session???
            session.setAttribute("user", user);

            // ??????cookie?????????????????????
            String userticket = loginUserRequest.getUsername();
            Cookie cookie_user = new Cookie("userticket", userticket);
            cookie_user.setMaxAge(60*60);//??????????????????1?????????????????????
            cookie_user.setPath(request.getContextPath());//??????cookie????????????
            response.addCookie(cookie_user);// ??????????????????cookie

            return "redirect:main";
//            return "mainIndex";
        }
    }
    /****************************  Log out  **************************/
    @RequestMapping("/user/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // ??????cookie
        Cookie userticket = new Cookie("userticket", "");
        userticket.setMaxAge(0);
        userticket.setPath(request.getContextPath());
        response.addCookie(userticket);

        HttpSession session = request.getSession();
        // ??????????????????session?????????
        session.removeAttribute("user");

        Object info = session.getAttribute("user");
        if(info==null){
            System.out.println("logout success");
        }else{
            System.out.println("failed to logout");
        }
        return "index";
    }

    public void getRecs(ModelAndView mv) throws ExecutionException, InterruptedException {
        HotMovieRequest hotMovieRequest = new HotMovieRequest(6);//??????6???
        CompletableFuture<List<MovieVO>> hotMovieVOS = recService.getHotRecommendations(hotMovieRequest);
        LatestMovieRequest latestMovieRequest = new LatestMovieRequest(6);//??????6???
        CompletableFuture<List<MovieVO>> latestMovieVOS = recService.getLatestRecommendations(latestMovieRequest);

        CompletableFuture.allOf(hotMovieVOS, latestMovieVOS).join();
        List<MovieVO> hotmovies = hotMovieVOS.get();
        List<MovieVO> latestmovies = hotMovieVOS.get();
        mv.addObject("rechotmovieVOS", hotmovies);
        mv.addObject("reclatestmovieVOS", latestmovies);
        return ;
    }

    public void getRecs2(Model model) throws ExecutionException, InterruptedException {
        HotMovieRequest hotMovieRequest = new HotMovieRequest(6);//??????6???
        CompletableFuture<List<MovieVO>> hotMovieVOS = recService.getHotRecommendations(hotMovieRequest);
        LatestMovieRequest latestMovieRequest = new LatestMovieRequest(6);//??????6???
        CompletableFuture<List<MovieVO>> latestMovieVOS = recService.getLatestRecommendations(latestMovieRequest);

        CompletableFuture.allOf(hotMovieVOS, latestMovieVOS).join();
        List<MovieVO> hotmovies = hotMovieVOS.get();
        List<MovieVO> latestmovies = hotMovieVOS.get();

        model.addAttribute("rechotmovieVOS", hotmovies);
        model.addAttribute("reclatestmovieVOS", latestmovies);
        return ;
    }



}


