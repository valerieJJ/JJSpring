package valerie.mycontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import valerie.myModel.Movie;
import valerie.myservices.MovieService;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/movie")
@Controller
public class MovieController {
    @Autowired
    private MovieService movieService;
    /**
     * 获取单个电影的信息
     */
//    @RequestMapping("/getmovie")
//    public ModelAndView getMovieInfo(ModelAndView modelAndView) {
//        int id = 2549;
//        modelAndView.addObject("success",true);
//        Movie movie = movieService.findByMID(id);
//        if(movie==null){
//            System.out.println("movie not found");
//            modelAndView.setViewName("show");
//        }else {
//            System.out.println(movie.toString());
//            modelAndView.addObject("movie",movie);
//        }
//        modelAndView.setViewName("movieInfo");
//        return modelAndView;
//    }


    @RequestMapping("/getmovie")
    public ModelAndView getMovieInfo(@ModelAttribute("movie") Movie movieReq, ModelAndView modelAndView) {
        int mid = movieReq.getMid();
        System.out.println("getmovie - get mid = "+mid);
        if(mid==0){
            mid = 2549;
        }

        modelAndView.addObject("success",true);
        Movie movie = movieService.findByMID(mid);
        if(movie==null){
            System.out.println("movie not found");
            modelAndView.setViewName("show");
        }else {
//            System.out.println(movie.toString());
            modelAndView.addObject("movie",movie);
        }
        modelAndView.setViewName("movieInfo");
        return modelAndView;
    }
}
