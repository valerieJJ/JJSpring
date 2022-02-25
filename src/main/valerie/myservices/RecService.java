package valerie.myservices;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.*;
import com.mongodb.client.model.Sorts;
import com.mongodb.util.JSON;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import valerie.myModel.Rating;
import valerie.myModel.VO.MovieVO;
import valerie.myModel.Movie;
import valerie.myModel.DTO.MovieDTO;
import valerie.myModel.requests.HotMovieRequest;
import valerie.myModel.requests.LatestMovieRequest;
import valerie.myUtils.Constant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecService {
    @Autowired
    private MovieService movieService;
    @Autowired
    private MongodbService mongodbService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RatingService ratingService;

    private MovieDTO DBObject2MovieDTO(DBObject object){
        try{
            return objectMapper.readValue(JSON.serialize(object), MovieDTO.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
            return null;
        } catch (JsonMappingException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public List<MovieVO> getLatestRecommendations(LatestMovieRequest request) {
        int sum = request.getSum();
        System.out.println("latest recommendation getSum = " + sum);
        DBCollection movieCollection = mongodbService.getCollection("Movie");
//        BasicDBObject shoot =
//        DBCursor cursor = movieCollection.find().sort(Sorts.orderBy(Sorts.descending("times"))).limit(sum);

        DBCursor cursor = movieCollection.find().sort(new BasicDBObject("shoot", -1));//.sort(Sorts.descending("yearmonth")).limit(sum)

        List<MovieVO> movieVOS = new ArrayList<>();
        while (cursor.hasNext() && sum != 0) {
            MovieDTO movieDTO = DBObject2MovieDTO(cursor.next());
            int mid = movieDTO.getMid();
            MovieVO movieVO = new MovieVO();
            movieVO.setMid(mid);
            movieVO.setCountRates(movieDTO.getCount());
            Movie movie = movieService.findByMID(mid);
            String avgScore = ratingService.getMovieAverageScores(mid);
            movieVO.setName(movie.getName());
            movieVO.setScore(avgScore);
            movieVO.setDescri(movie.getDescri());
            movieVO.setDirectors(movie.getDirectors());
            movieVO.setActors(movie.getActors());
            movieVO.setGenres(movie.getGenres());
            movieVO.setLanguage(movie.getLanguage());
            movieVO.setIssue(movie.getIssue());
            movieVOS.add(movieVO);

            sum--;
        }
        return movieVOS;
    }

    public List<MovieVO> getHotRecommendations(HotMovieRequest request){
        int sum = request.getSum();
        System.out.println("hot recommendation getSum = "+sum);
        DBCollection movieCollection = mongodbService.getCollection("RateMoreMovies");
        DBCursor cursor = movieCollection.find().sort(new BasicDBObject("count",-1));//.sort(Sorts.descending("yearmonth")).limit(sum)
        List<MovieVO> movieVOS = new ArrayList<>();
        while (cursor.hasNext() && sum!=0){
            MovieDTO movieDTO = DBObject2MovieDTO(cursor.next());
            int mid = movieDTO.getMid();
            MovieVO movieVO = new MovieVO();
            movieVO.setMid(mid);
            movieVO.setCountRates(movieDTO.getCount());
            Movie movie = movieService.findByMID(mid);
            String avgScore = ratingService.getMovieAverageScores(mid);
            movieVO.setName(movie.getName());
            movieVO.setScore(avgScore);
            movieVO.setDescri(movie.getDescri());
            movieVO.setDirectors(movie.getDirectors());
            movieVO.setActors(movie.getActors());
            movieVO.setGenres(movie.getGenres());
            movieVO.setLanguage(movie.getLanguage());
            movieVO.setIssue(movie.getIssue());
            movieVOS.add(movieVO);

            sum--;
        }
        return movieVOS;

    }


}
