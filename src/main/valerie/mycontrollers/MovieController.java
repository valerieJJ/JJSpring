package valerie.mycontrollers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import valerie.myModel.DTO.MovieDTO;
import valerie.myModel.Movie;
import valerie.myModel.Rating;
import valerie.myModel.User;
import valerie.myModel.VO.MovieVO;
import valerie.myModel.requests.FavoriteRequest;
import valerie.myModel.requests.MovieRatingRequest;
import valerie.myservices.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

@RequestMapping("/movie")
@Controller
public class MovieController {
    @Autowired
    private MovieService movieService;
    @Autowired
    private UserService userService;
    @Autowired
    private RatingService ratingService;
    @Autowired
    private ESService esService;
    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private MultiThreadsService multiThreadsService;

    @RequestMapping("rate")
    public String rateMovie(
            @ModelAttribute("rating") Rating ratingReq,
            Model model,
            HttpServletRequest request
    ) throws JsonProcessingException, IllegalAccessException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        int mid = ratingReq.getMid();
        double score = ratingReq.getScore();

        MovieRatingRequest ratingRequest = new MovieRatingRequest(user.getUid(), mid, score);
        boolean done = ratingService.updataMovieRating(ratingRequest);
        if(done){
            model.addAttribute("rating_message", "rating successful");
            System.out.println("rating updated successful!");
            System.out.println("user "+user.getUsername());
            System.out.println("mid = "+mid);
            System.out.println("score = "+score);
        }else {
            model.addAttribute("rating_message", "rating failed");
        }

        Movie movie = (Movie)session.getAttribute("movie");

        String movie_score = ratingService.getMovieAverageScores(mid);
        model.addAttribute("movie", movie);

        model.addAttribute("movie_score", movie_score);
        return "movieInfo";
    }

    @RequestMapping("/moviefolder")
    public ModelAndView goMovieFolder(String type) throws UnknownHostException {
        ModelAndView modelAndView = new ModelAndView();

        System.out.println("~~~ go to "+ type);
//        String field="language";
        String field = "genres";
        List<Movie> data = movieService.getCollectionData(field, type);
        modelAndView.addObject("movies", data);
        modelAndView.addObject("folder_name", type);
        modelAndView.setViewName("movieFolder");
//        HttpSession session = request.getSession();
//        User usr = (User)session.getAttribute("user");
//        modelAndView.addObject("user", usr);
        return modelAndView;
    }
    @RequestMapping("/moviefolder1")
    public ModelAndView showMovieFolder(HttpServletRequest request) throws UnknownHostException {
        ModelAndView modelAndView = new ModelAndView();
//        String field="language";
//        String value = "English";

        String field="genres";
        String value = "Comedy";
        List<Movie> data = movieService.getCollectionData(field, value);
        modelAndView.addObject("movies", data);
        modelAndView.addObject("folder_name", value);
        modelAndView.setViewName("movieFolder");
//        HttpSession session = request.getSession();
//        User usr = (User)session.getAttribute("user");
//        modelAndView.addObject("user", usr);
        return modelAndView;
    }

    @RequestMapping("/moviefolder2")
    public ModelAndView showMovieFolder2(HttpServletRequest request) throws UnknownHostException {
        ModelAndView modelAndView = new ModelAndView();
        String field="genres";
        String value = "Action";
        List<Movie> data = movieService.getCollectionData(field, value);
        modelAndView.addObject("movies", data);
        modelAndView.addObject("folder_name", value);
        modelAndView.setViewName("movieFolder");
//        HttpSession session = request.getSession();
//        User usr = (User)session.getAttribute("user");
//        modelAndView.addObject("user", usr);
        return modelAndView;
    }

    @RequestMapping("/moviefolder3")
    public ModelAndView showMovieFolder3(HttpServletRequest request) throws UnknownHostException {
        ModelAndView modelAndView = new ModelAndView();
        String field="genres";
        String value = "Drama";
        List<Movie> data = movieService.getCollectionData(field, value);
        modelAndView.addObject("movies", data);
        modelAndView.addObject("folder_name", value);
        modelAndView.setViewName("movieFolder");
//        HttpSession session = request.getSession();
//        User usr = (User)session.getAttribute("user");
//        modelAndView.addObject("user", usr);
        return modelAndView;
    }

    @RequestMapping("/movieid")
    public ModelAndView getMovieInfo(
//            @ModelAttribute("movie") Movie movieReq
            @ModelAttribute("mid") int mid
            ,@ModelAttribute("rating") Rating rating
            , HttpServletRequest request
            , ModelAndView modelAndView) throws ExecutionException, InterruptedException {
//        int mid = movieReq.getMid();
        System.out.println("getmovie - get mid = "+mid);

//        Movie movie = movieService.findByMID(mid);
//        String movie_score = ratingService.getMovieAverageScores(mid);

        CompletableFuture<Movie> asy_movie = movieService.asyfindByMID(mid);
        CompletableFuture<String> asy_movie_score = ratingService.asygetMovieAverageScores(mid);
        CompletableFuture.allOf(asy_movie,asy_movie_score).join();
        Movie movie = asy_movie.get();
        String movie_score = asy_movie_score.get();

        if(movie==null){
            System.out.println("movie not found");
            modelAndView.setViewName("show");
        }else {
            modelAndView.addObject("movie",movie);
            modelAndView.addObject("movie_score", movie_score);
        }
        modelAndView.addObject("rating_message", "how do u like it?");
        HttpSession session = request.getSession();
        session.setAttribute("movie", movie);


        User user = (User) session.getAttribute("user");
        boolean state = favoriteService.favoriteExistMongo(user.getUid(), mid);
        modelAndView.addObject("state", state);

        System.out.println("get state2: "+ state);
        modelAndView.setViewName("movieInfo");
        return modelAndView;
    }

    @RequestMapping("/moviefield")
    public ModelAndView searchMovieByName(String fieldname
                                          ,String value
            , HttpServletRequest request) throws IOException {
        ModelAndView modelAndView = new ModelAndView();
        System.out.println("search movie field = "+fieldname);
        System.out.println("search value = " + value);
        String es_collection = "movietags";
        String[] excludes = {};
        String[] includes = {"mid", "name", "genres", "language", "descri", "issue", "shoot", "directors", "timelong"};

        HashMap<List<MovieDTO>, String> map;

        map = esService.fullQuery("match", es_collection,
                fieldname, value, excludes, includes, 6);
//        if (fieldname=="genres"){
//            map = esService.fullQuery("match", es_collection, fieldname, value, excludes, includes, 10);
//        }else if(fieldname=="name"){
//            map = esService.fullQuery("match", es_collection,fieldname, "Godfather: Part III", excludes, includes, 10);
//        }else{
//            map = esService.fullQuery("wildCard", es_collection, fieldname, value, excludes, includes, 10);
//        }

        Set<List<MovieDTO>> set = map.keySet();
        List<MovieVO> movieVOList = new ArrayList<>();
        for(List<MovieDTO> list: set){
            for(MovieDTO movieDTO: list){
                Integer mid = movieDTO.getMid();
                MovieVO movieVO = new MovieVO();
                Movie mov = movieService.findByMID(mid);
                String movie_score = ratingService.getMovieAverageScores(mid);
                movieVO.setMid(mid);
                movieVO.setName(mov.getName());
                movieVO.setScore(movie_score);
                movieVO.setIssue(mov.getIssue());
                movieVO.setGenres(mov.getGenres());
//                movieVO.setLanguage(mov.getLanguage());
//                movieVO.setDirectors(mov.getDirectors());
//                movieVO.setDescri(mov.getDescri());
//                movieVO.setShoot(mov.getShoot());
                movieVOList.add(movieVO);
            }
        }
        System.out.println("get movieVOList.size() = "+movieVOList.size());
        System.out.println("get movieVOList.get(0) = "+movieVOList.get(0).getName());

        if(movieVOList==null){
            System.out.println("movie not found");
            modelAndView.setViewName("show");
        }else {
            System.out.println("goto moviename = "+ movieVOList.get(0));
            modelAndView.addObject("movieVOList",movieVOList);
            modelAndView.addObject("number",movieVOList.size());
            modelAndView.addObject("fieldname",fieldname);
            modelAndView.addObject("value", value);
            modelAndView.setViewName("movieList");
            HttpSession session = request.getSession();
            session.setAttribute("movieVOList", movieVOList);
        }
        return modelAndView;
    }


    @RequestMapping("/favor")
    public void doFavor(
            HttpServletRequest request
            , Model model) {
        int mid = (int) request.getAttribute("mid");
        System.out.println("getmovie - get mid = "+mid);

        Movie movie = movieService.findByMID(mid);
        if(movie==null){
        }else {
            model.addAttribute("movie",movie);
        }
        HttpSession session = request.getSession();
        session.setAttribute("movie", movie);

        User user = (User) session.getAttribute("user");
        boolean state = favoriteService.favoriteExistMongo(user.getUid(), mid);
        model.addAttribute("state", state);

        System.out.println("get state2: "+ state);
    }


}
