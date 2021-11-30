package valerie.mycontrollers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import valerie.myModel.Movie;
import valerie.myModel.Rating;
import valerie.myModel.User;
import valerie.myModel.requests.MovieRatingRequest;
import valerie.myservices.MovieService;
import valerie.myservices.RatingService;
import valerie.myservices.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.UnknownHostException;
import java.util.List;

@RequestMapping("/movie")
@Controller
public class MovieController {
    @Autowired
    private MovieService movieService;
    @Autowired
    private UserService userService;
    @Autowired
    private RatingService ratingService;

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
        model.addAttribute("movie", movie);
        return "movieInfo";
    }

    @RequestMapping("/moviefolder1")
    public ModelAndView showMovieFolder(HttpServletRequest request) throws UnknownHostException {
        ModelAndView modelAndView = new ModelAndView();
        String field="language";
        String value = "English";
        List<Movie> data = movieService.getCollectionData(field, value);
        modelAndView.addObject("movies", data);
        modelAndView.addObject("folder_name", "Pop Song Folder");
        modelAndView.setViewName("movieFolder");
//        HttpSession session = request.getSession();
//        User usr = (User)session.getAttribute("user");
//        modelAndView.addObject("user", usr);
        return modelAndView;
    }

    @RequestMapping("/getmovie")
    public ModelAndView getMovieInfo(
            @ModelAttribute("movie") Movie movieReq
            ,@ModelAttribute("rating") Rating rating
            , HttpServletRequest request
            , ModelAndView modelAndView) {
        int mid = movieReq.getMid();
        System.out.println("getmovie - get mid = "+mid);
        if(mid==0){
            mid = 2549;
        }
        Movie movie = movieService.findByMID(mid);
        if(movie==null){
            System.out.println("movie not found");
            modelAndView.setViewName("show");
        }else {
            modelAndView.addObject("movie",movie);
        }
        modelAndView.addObject("rating_message", "how do u like it?");
        modelAndView.setViewName("movieInfo");
        HttpSession session = request.getSession();
        session.setAttribute("movie", movie);
        return modelAndView;
    }



}
