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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

//@Controller
public class RecMovieController {
    @Autowired
    private RecService recService;

    @RequestMapping("/")
    public ModelAndView recHotMovies() throws ExecutionException, InterruptedException {
        System.out.println("recHotMovies////");

        HotMovieRequest hotMovieRequest = new HotMovieRequest(6);//取出6个
        CompletableFuture<List<MovieVO>> hotMovieVOS = recService.getHotRecommendations(hotMovieRequest);
        LatestMovieRequest latestMovieRequest = new LatestMovieRequest(6);//取出6个
        CompletableFuture<List<MovieVO>> latestMovieVOS = recService.getLatestRecommendations(latestMovieRequest);

        CompletableFuture.allOf(hotMovieVOS, latestMovieVOS).join();
        List<MovieVO> hotmovies = hotMovieVOS.get();
        List<MovieVO> latestmovies = latestMovieVOS.get();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("rechotmovieVOS",hotmovies);

        modelAndView.addObject("reclatestmovieVOS",latestmovies);


        modelAndView.setViewName("index");
        return modelAndView;
    }

}
