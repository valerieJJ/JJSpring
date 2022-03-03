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

import javax.servlet.http.HttpServletRequest;
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
            model.addAttribute("message"," 用户名已经被注册！");
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
    public String login(@ModelAttribute("user") User user, @ModelAttribute("movie") Movie movieReq, Model model, HttpServletRequest request) throws UnknownHostException, ExecutionException, InterruptedException {
        User newUsr = userService.loginUser(new LoginUserRequest(user.getUsername(),user.getPassword()));

        if(newUsr==null){
            System.out.println("Account does not exist");
            return "login";
        }else {
            System.out.println("\nGet username="+newUsr.getUsername());
            System.out.println("Get password="+newUsr.getPassword());
            HttpSession session = request.getSession();
            session.setAttribute("user", newUsr);
            return "redirect:goindex";
//            return "mainIndex";
        }
    }


    public void getRecs(ModelAndView mv) throws ExecutionException, InterruptedException {
        HotMovieRequest hotMovieRequest = new HotMovieRequest(6);//取出6个
        CompletableFuture<List<MovieVO>> hotMovieVOS = recService.getHotRecommendations(hotMovieRequest);
        LatestMovieRequest latestMovieRequest = new LatestMovieRequest(6);//取出6个
        CompletableFuture<List<MovieVO>> latestMovieVOS = recService.getLatestRecommendations(latestMovieRequest);

        CompletableFuture.allOf(hotMovieVOS, latestMovieVOS).join();
        List<MovieVO> hotmovies = hotMovieVOS.get();
        List<MovieVO> latestmovies = hotMovieVOS.get();
        mv.addObject("rechotmovieVOS", hotmovies);
        mv.addObject("reclatestmovieVOS", latestmovies);
        return ;
    }

    public void getRecs2(Model model) throws ExecutionException, InterruptedException {
        HotMovieRequest hotMovieRequest = new HotMovieRequest(6);//取出6个
        CompletableFuture<List<MovieVO>> hotMovieVOS = recService.getHotRecommendations(hotMovieRequest);
        LatestMovieRequest latestMovieRequest = new LatestMovieRequest(6);//取出6个
        CompletableFuture<List<MovieVO>> latestMovieVOS = recService.getLatestRecommendations(latestMovieRequest);

        CompletableFuture.allOf(hotMovieVOS, latestMovieVOS).join();
        List<MovieVO> hotmovies = hotMovieVOS.get();
        List<MovieVO> latestmovies = hotMovieVOS.get();

        model.addAttribute("rechotmovieVOS", hotmovies);
        model.addAttribute("reclatestmovieVOS", latestmovies);
        return ;
    }



}


