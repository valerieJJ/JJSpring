package valerie.myservices;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.*;
import com.mongodb.client.model.Sorts;
import com.mongodb.tools.*;
import com.mongodb.client.model.Filters;

import com.mongodb.util.JSON;
import org.bson.Document;

import org.bson.Document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import valerie.myModel.Movie;
import valerie.myModel.Rating;
import valerie.myModel.Recommendation;
import valerie.myModel.VO.MovieVO;
import valerie.myModel.requests.NewRecommendationRequest;
//import valerie.myUtils.Constant;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class MovieService {
    @Autowired
    private MongoClient mongoClient;
    @Autowired
    private MongodbService mongodbService;
    @Autowired
    private RatingService ratingService;

    @Autowired
    private ObjectMapper objectMapper;

    private DBCollection averageMoviesScoreCollection;
    private DBCollection rateCollection;
    private DBCollection movieCollection;

    public DBCollection getMovieCollection(){

        if(null == movieCollection){
            DB db = mongoClient.getDB( "MovieDB" );
            Set<String> collections = db.getCollectionNames();
            movieCollection = db.getCollection("Movie");
        }
        return movieCollection;
    }

    public List<Movie> getCollectionData(String field, String value) throws UnknownHostException {
        List<Movie> res = mongodbService.getDataObj(field,value);
        return res;
    }

    private DBCollection getAverageMoviesScoreCollection(){
        if(null == averageMoviesScoreCollection)
            averageMoviesScoreCollection = mongoClient.getDB("MovieDB")
                    .getCollection("AverageMovies");//Constant.MONGODB_AVERAGE_MOVIES_SCORE_COLLECTION
        return averageMoviesScoreCollection;
    }

    private DBCollection getRateCollection(){
        if(null == rateCollection)
            rateCollection = mongoClient.getDB("MovieDB").getCollection("Rating");//Constant.MONGODB_RATING_COLLECTION
        return rateCollection;
    }

    public List<Movie> getRecommendeMovies(List<Recommendation> recommendations){
        List<Integer> ids = new ArrayList<>();
        for (Recommendation rec: recommendations) {
            ids.add(rec.getMid());
        }
        return getMovies(ids);
    }

    public List<Movie> getHybirdRecommendeMovies(List<Recommendation> recommendations){
        List<Integer> ids = new ArrayList<>();
        for (Recommendation rec: recommendations) {
            ids.add(rec.getMid());
        }
        return getMovies(ids);
    }

    public List<MovieVO> getMovieVOS(List<Integer> mids){
        List<MovieVO> movieVOS = new ArrayList<>();
        for(Integer mid: mids){
            MovieVO movieVO = new MovieVO();
            movieVO.setMid(mid);
            Movie movie = findByMID(mid);
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
        }
        return movieVOS;
    }

    public List<Movie> getMovies(List<Integer> mids){
        List<Movie> movies = new ArrayList<>();
//        FindIterable<Document> documents = getMovieCollection().find(Filters.in("mid",mids));
//        DBCursor cursor = (DBCursor) getMovieCollection().find(new BasicDBObject("mid", mids));

        DBObject query = (DBObject) Filters.in("mid",mids);
        DBCursor cursor = getMovieCollection().find(query);
        while(cursor.hasNext()){
            DBObject movieObj = cursor.next();
            Movie movie = (Movie)movieObj;
            movies.add(movie);
        }
        return movies;
    }

    private Movie DBObject2Movie(DBObject object){
        try{
            return objectMapper.readValue(JSON.serialize(object), Movie.class);
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
    private Rating DBObject2Rating(DBObject object){
        Rating rating = null;
        try{
            rating = objectMapper.readValue(JSON.serialize(object),Rating.class);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return rating;
    }

    public boolean movieExist(int mid){
        return null != findByMID(mid);
    }

    public Movie findByMID(int mid){

        DBObject query = new BasicDBObject("mid", mid);
        DBCursor cursor = getMovieCollection().find(query);
        if(cursor.hasNext()){
            DBObject obj = cursor.next();
            return this.DBObject2Movie(obj);
        }
        System.out.println("movieID not exist");
        return null;
    }

    @Async
    public CompletableFuture<Movie> asyfindByMID(int mid){
        System.out.println("Async finding movie by mID...");
        DBObject query = new BasicDBObject("mid", mid);
        DBCursor cursor = getMovieCollection().find(query);
        if(cursor.hasNext()){
            DBObject obj = cursor.next();
            return CompletableFuture.completedFuture(this.DBObject2Movie(obj));
        }
        System.out.println("movieID not exist");
        return null;
    }

    public List<Movie> getDataObj(String field, String value) throws UnknownHostException {
        DB db = mongoClient.getDB("MovieDB");
        DBCollection coll = db.getCollection("Movie");
        DBObject query = new BasicDBObject(field, value);

        DBCursor cursor = coll.find(query);
        List<Movie> movies = new ArrayList<>();
        int cnt = 0;
        while (cursor.hasNext() && cnt <= 3) {
            DBObject movieObj = cursor.next();
            Movie movie = this.DBObject2Movie(movieObj);
            movies.add(movie);
            cnt++;
        }

        System.out.println(movies);
        return movies;
    }

    public void removeMovie(int mid){
        DBObject query = new BasicDBObject("mid",mid);
        getMovieCollection().remove(query);
    }

    public List<Movie> getMyRateMovies(int uid){
        DBObject query = new BasicDBObject("uid",uid);
        DBCursor cursor = getRateCollection().find(query);
        List<Integer> ids = new ArrayList<>();
        Map<Integer,Double> scores = new HashMap<>();
        while (cursor.hasNext()){
            DBObject ratingObj = cursor.next();
            Rating rating = DBObject2Rating(ratingObj);
            ids.add(rating.getMid());
            scores.put(rating.getMid(),rating.getScore());
        }

        List<Movie> movies = getMovies(ids);
        for (Movie movie: movies) {
            movie.setScore(scores.getOrDefault(movie.getMid(),movie.getScore()));
        }

        return movies;
    }

    public List<Movie> getNewMovies(NewRecommendationRequest request){
        DBObject query = new BasicDBObject();
        DBCursor cursor = getMovieCollection().find()
                .sort((DBObject) Sorts.descending("issue")).limit(request.getSum());
        List<Movie> movies = new ArrayList<>();
        while (cursor.hasNext()){
            DBObject movieObj = cursor.next();
            movies.add(DBObject2Movie(movieObj));
        }
        return movies;
    }

    public HashMap<String, String> getMovieTypes(){
        String[] cls = {"Comedy","Action","Drama"};
        String[] describ = {"Comedies bring laughter", "Exciting action movies", "Warm Dramas for you"};
        HashMap<String, String> map = new HashMap<>();
        for(int i=0;i<cls.length;i++){
            map.put(cls[i], describ[i]);
        }

//        ArrayList<String> classes = new ArrayList<>(Arrays.asList(cls));
        return map;
    }
}
