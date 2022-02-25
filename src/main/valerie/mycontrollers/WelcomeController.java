package valerie.mycontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import valerie.myModel.VO.MovieVO;
import valerie.myModel.requests.HotMovieRequest;
import valerie.myModel.requests.LatestMovieRequest;
import valerie.myservices.*;

import java.util.List;

@Controller
public class WelcomeController {
    @Autowired
    private RecService recService;

    @RequestMapping("")
    public ModelAndView welcomePage(){

        HotMovieRequest hotMovieRequest = new HotMovieRequest(6);//取出6个
        List<MovieVO> movieVOS = recService.getHotRecommendations(hotMovieRequest);

        LatestMovieRequest latestMovieRequest = new LatestMovieRequest(6);//取出6个
        List<MovieVO> latestMovieVOS = recService.getLatestRecommendations(latestMovieRequest);

        ModelAndView mv = new ModelAndView();
        mv.addObject("latestmovieVOS", latestMovieVOS);
        mv.addObject("hotmovieVOS", movieVOS);
        mv.setViewName("index");
        return mv;
    }
}
