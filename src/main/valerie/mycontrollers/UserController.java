package valerie.mycontrollers;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
//import valerie.mycontrollers.MongodbService;
import valerie.myModel.Favorite;
import valerie.myModel.Movie;
import valerie.myModel.User;
import valerie.myModel.VO.MovieVO;
import valerie.myModel.requests.*;
import valerie.myservices.FavoriteService;
import valerie.myservices.MovieService;
import valerie.myservices.RecService;
import valerie.myservices.UserService;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

@RequestMapping("/user")
@Controller
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private MovieService movieService;
    @Autowired
    private RecService recService;
    @Autowired
    private FavoriteService favoriteService;

    public UserController() throws UnknownHostException {
    }

    @RequestMapping("/show")
    public String getData(@ModelAttribute("movie") Movie movie, Model model,HttpServletRequest request) throws UnknownHostException {

        String field="language";
        String value = "English";
        List<Movie> data = userService.getCollectionData(field, value);
        model.addAttribute("mongodata", data.toString());

        HttpSession session1 = request.getSession();
        User usr = (User)session1.getAttribute("user");
        if(usr == null){
            usr = userService.getDefaultUser();
        }
        model.addAttribute("user", usr);

        System.out.println("show - port:"+request.getServerPort()+",session:"+session1.getId());
        System.out.println("show - get user from request.session" + usr.getUsername());
        return "show";
    }


//    @RequestMapping("/accountPage")
//    public String goAccountPage(Model model, HttpServletRequest request) throws UnknownHostException {
//        HttpSession session = request.getSession();
//        System.out.println("accountPage - port:"+request.getServerPort()+",session:"+session.getId());
//        User user = (User)session.getAttribute("user");
//        if(user==null){
//            System.out.println("account - no log in");
//            return "index";
//        }else{
//            model.addAttribute("user", user);
//            return "accountPage";
//        }
//    }


    @RequestMapping("/whoisit")
    public String getNickname(Model model, HttpServletRequest request) throws UnknownHostException {
        HttpSession session = request.getSession();
        System.out.println("whoisit - port:"+request.getServerPort()+",session:"+session.getId());
        User user = (User)session.getAttribute("user");

        if(user==null){
            System.out.println("no log in");
            return "index";
        }else{
            model.addAttribute("user", user);
            return "whoisit";
        }
    }

    /****************************  Login  **************************/

//    @RequestMapping("/login")
//    public String login(Model model) throws UnknownHostException {
////        User user = userservice.getDefaultUser();
////        model.addAttribute("user", user);
//        return "login";
//    }

//    @GetMapping("/dologin")
//    @RequestMapping("")
    @RequestMapping("/dologin") //@RequestMapping("/user/dologin")
    public String login(@ModelAttribute("user") User user,@ModelAttribute("movie") Movie movieReq, Model model,HttpServletRequest request) throws UnknownHostException {
        User newUsr = userService.loginUser(new LoginUserRequest(user.getUsername(),user.getPassword()));

        if(newUsr==null){
            System.out.println("Account does not exist");
//            Main.Result.builder().code(-1).msg("用户名或密码错误").build();
            return "login";
        }else {
            System.out.println("\nGet username="+newUsr.getUsername());
            System.out.println("Get password="+newUsr.getPassword());

            LatestMovieRequest latestMovieRequest = new LatestMovieRequest(6);//取出6个
            List<MovieVO> latestMovieVOS = recService.getLatestRecommendations(latestMovieRequest);
            model.addAttribute("latestmovieVOS", latestMovieVOS);

            HotMovieRequest hotMovieRequest = new HotMovieRequest(6);
            List<MovieVO> movieVOS = recService.getHotRecommendations(hotMovieRequest);
            model.addAttribute("hotmovieVOS", movieVOS);

            Set<String> rank = favoriteService.getZsetRank();
            List<Integer> rankmids = rank.stream().limit(5).map(x->Integer.parseInt(x)).collect(Collectors.toList());
            List<MovieVO> rankMovieVOS = movieService.getMovieVOS(rankmids);
            model.addAttribute("rankmovieVOS", rankMovieVOS);

            HttpSession session = request.getSession();
            session.setAttribute("user", newUsr);
            model.addAttribute("user", newUsr);
            System.out.println("login - port:"+request.getServerPort()+",session:"+session.getId());

            return "mainIndex";
        }
    }

    /****************************  Log out  **************************/
    @RequestMapping("/logout")
    public String logout(HttpServletRequest request){
        HttpSession session = request.getSession();
        // 将用户信息从session中删除
        session.removeAttribute("user");
        Object info = session.getAttribute("user");
        if(info==null){
            System.out.println("logout success");
        }else{
            System.out.println("failed to logout");
        }
        return "index";
    }

    /****************************  Register  **************************/
    @RequestMapping("/register")
    public String register(){
        return "register";
    }
    @RequestMapping("/doregister")
    public String register(Model model, HttpServletRequest request){
        String name = request.getParameter("username");
        String password = request.getParameter("password");

        System.out.println("username is " + name);
        System.out.println("password is " + password);
        HotMovieRequest hotMovieRequest = new HotMovieRequest(6);
        List<MovieVO> movieVOS = recService.getHotRecommendations(hotMovieRequest);
//        User user = userservice.getUser(new RegisterUserRequest(name, password));
        if(userService.checkUserExist(name)){
            System.out.println("user already exists, please login");
            model.addAttribute("success",false);
            model.addAttribute("message"," 用户名已经被注册！");
            return "register";
        }else{
            model.addAttribute("success", true);
            User user = userService.registerUser(new RegisterUserRequest(name,password));
            model.addAttribute("user", user);
            model.addAttribute("hotmovieVOS", movieVOS);
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            System.out.println("register successful!");
        }
        return "mainIndex";
    }

    @RequestMapping("/account")
    public String accountPage(Model model, HttpServletRequest request){//

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        System.out.println("account action: sessionID" + session.getId());
        HashMap<String, String> movie_types = movieService.getMovieTypes();
        if(user==null){
            System.out.println("please log in first");
            return "index";
        }else{
            System.out.println("account action: user " + user.getUsername());
            model.addAttribute("user", user);
            model.addAttribute("movie_types", movie_types);
        }

        List<Favorite> favoriteList = favoriteService.getFavoriteHistory(user.getUid());
        List<Integer> mids = new ArrayList<>();
        for (Favorite favorite: favoriteList){
            Integer mid = favorite.getMid();
            mids.add(mid);
        }
        List<MovieVO> favoriteMovieVOS =movieService.getMovieVOS(mids);
        model.addAttribute("favoriteMovieVOS", favoriteMovieVOS);

        return "accountPage";
    }

    @RequestMapping("/goindex")
    public ModelAndView goIndex(@ModelAttribute("movie") Movie movie, HttpServletRequest request){
        HttpSession session = request.getSession();
        ModelAndView mv = new ModelAndView();
        User user = (User) session.getAttribute("user");

        HotMovieRequest hotMovieRequest = new HotMovieRequest(6);//取出6个
        List<MovieVO> movieVOS = recService.getHotRecommendations(hotMovieRequest);

        LatestMovieRequest latestMovieRequest = new LatestMovieRequest(6);//取出6个
        List<MovieVO> latestMovieVOS = recService.getLatestRecommendations(latestMovieRequest);

        Set<String> rank = favoriteService.getZsetRank();
        List<Integer> rankmids = rank.stream().limit(5).map(x->Integer.parseInt(x)).collect(Collectors.toList());
        List<MovieVO> rankMovieVOS = movieService.getMovieVOS(rankmids);

        System.out.print("latest count: "+latestMovieVOS.size());
        latestMovieVOS.stream().forEach(x->System.out.println("latest: "+x.getName()+", "+x.getShoot()));

        if(user==null){// || session.getAttribute("user")==null
            mv.setViewName("index");
            System.out.println("no log in");
        }else{
            System.out.println("goIndex: username = "+user.getUsername());
            mv.addObject("user", user);
            mv.addObject("latestmovieVOS", latestMovieVOS);
            mv.addObject("hotmovieVOS", movieVOS);
            mv.addObject("rankmovieVOS", rankMovieVOS);
            mv.setViewName("mainIndex");
        }
        return mv;
    }

    @RequestMapping(value = "/{mid}/favor", produces = "application/json", method = RequestMethod.POST )
    public void doFavorite(
            @PathVariable("mid")int mid,
            @RequestParam("favoption")boolean favoption
            , HttpServletRequest request
            , HttpServletResponse response
            , Model model) throws IllegalAccessException, ServletException, IOException {
//            User user = userService.findByUsername(username);
        System.out.print("doFavorite......");
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            FavoriteRequest favoriteRequest = new FavoriteRequest(user.getUid(), mid);
            boolean succ = false;

            if(favoption){
                succ = favoriteService.updateFavorite(favoriteRequest);
            }else {
                succ = favoriteService.dropFavorite(favoriteRequest);
            }
            boolean state = (favoriteService.findFavorite2Mongo(user.getUid(), mid)==null)?false:true;
//            boolean state = (favoriteService.findFavorite(user.getUid(), mid)==null)?false:true;
            model.addAttribute("success",succ);
            model.addAttribute("state", state);
//            rank.forEach(x->System.out.println("zset rank: "+x));

//            System.out.println("\n\n is " + String.valueOf(state)+"\n");
        String toUrl = "/movie/movieid?mid="+mid;
        request.getRequestDispatcher(toUrl).forward(request, response);
    }

}
