package valerie.mycontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import valerie.myModel.DTO.MovieDTO;
import valerie.myModel.VO.MovieVO;
import valerie.myModel.requests.HotMovieRequest;
import valerie.myModel.requests.LatestMovieRequest;
import valerie.myservices.RecService;

import java.util.List;

@Controller
public class RecMovieController {
    @Autowired
    private RecService recService;

    @RequestMapping("/")
    public ModelAndView recHotMovies(){
        System.out.println("recHotMovies////");

        HotMovieRequest hotMovieRequest = new HotMovieRequest(6);
        List<MovieVO> movieVOS = recService.getHotRecommendations(hotMovieRequest);

        LatestMovieRequest latestMovieRequest = new LatestMovieRequest(6);//取出6个
        List<MovieVO> latestMovieVOS = recService.getLatestRecommendations(latestMovieRequest);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("rechotmovieVOS", movieVOS);

        modelAndView.addObject("reclatestmovieVOS",latestMovieVOS);


        modelAndView.setViewName("index");
        return modelAndView;
    }

}
