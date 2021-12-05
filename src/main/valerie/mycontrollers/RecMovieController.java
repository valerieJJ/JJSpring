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
import valerie.myservices.RecService;

import java.util.List;

//@Controller
public class RecMovieController {
    @Autowired
    private RecService recService;

    @RequestMapping("/rec/hotmovies")
    public ModelAndView recHotMovies(@RequestParam("amount")int amount, Model model){
        HotMovieRequest request = new HotMovieRequest(amount);
        List<MovieVO> recommendations = recService.getHotRecommendations(request);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("hotmovies", recommendations);
        modelAndView.setViewName("movieRec");
        return modelAndView;
    }

}
