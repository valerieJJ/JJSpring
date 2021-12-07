package valerie.myservices;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.*;
import com.mongodb.util.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import scala.Int;
import valerie.myModel.Movie;
import valerie.myModel.Rating;
import valerie.myModel.requests.MovieRatingRequest;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;

@Service
public class RatingService {

    private MongoClient mongoClient = new MongoClient( "localhost", 27017);

    @Autowired
    private MongodbService mongodbService;
    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private Jedis jedis;

    private DBCollection ratingCollection;

    public RatingService() throws UnknownHostException {
    }

    private Rating DBObject2Rating(DBObject object){
        try{
            return objectMapper.readValue(JSON.serialize(object), Rating.class);
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

    private DBCollection getRatingCollection(){
        DB db = mongoClient.getDB("MovieDB");
        ratingCollection = db.getCollection("Rating");
//        ratingCollection = mongodbService.getCollection("Rating");
        return ratingCollection;
    }

    private void updateRatingRedis(Rating rating){
        // 刷新缓存时，先删缓存再写缓存
        if(jedis.exists("uid:"+rating.getUid())
                && jedis.llen("uid:"+rating.getUid())>=40){
            jedis.rpop("uid:"+rating.getUid());
        }
        jedis.lpush("uid:"+rating.getUid(),rating.getMid()+":"+rating.getScore());
    }

    public String getMovieAverageScores(int mid){
        BasicDBObject query = new BasicDBObject();
        query.append("mid", mid);
        DBCursor cursor = getRatingCollection().find(query);
        double score = 0;
        int cnt = 0;
        while(cursor.hasNext()){
            Rating rating = DBObject2Rating(cursor.next()) ;
            double curScore = rating.getScore();
            score += curScore;
            cnt ++;
        }
        double avgScore = score/cnt;
        return String.format("%.2f", avgScore).toString();
    }

    private Rating findRating(int uid, int mid){
        BasicDBObject query = new BasicDBObject();
        query.append("uid", uid);
        query.append("mid", mid);
        DBCursor cursor = getRatingCollection().find(query);
        if (cursor.count()>0){
            System.out.println("you haven scored it before");
            Rating rating = DBObject2Rating(cursor.next());
            return rating;
        }else {
            return null;
        }
    }

    private List<Rating> findRatingByUID(int uid){
        BasicDBObject query = new BasicDBObject();
        query.append("uid", uid);
        DBCursor cursor = getRatingCollection().find(query);
        List<Rating> ratingList = new ArrayList<>();
        while (cursor.hasNext()){
            DBObject object = cursor.next();
            Rating rating = DBObject2Rating(object);
            ratingList.add(rating);
        }
        return ratingList;
    }

    public boolean ratingExistMongo(int uid, int mid){
        if(findRating(uid, mid)!=null){
            return true;
        }else {
            return false;
        }
    }

    public boolean insertNewRating2Mongo(Rating rating) throws JsonProcessingException, IllegalAccessException {
        DBObject obj = mongodbService.bean2DBObject(rating);
        getRatingCollection().insert(obj);
        return true;
    }

    public boolean updataMovieRating(MovieRatingRequest request) throws JsonProcessingException, IllegalAccessException {
        Rating rating = new Rating(request.getUid(), request.getMid(),request.getScore());
        updateRatingRedis(rating);
        if (ratingExistMongo(rating.getUid(),rating.getMid())){
            return updateRatingMongo(rating);
        }else {
            return insertNewRating2Mongo(rating);
        }
    }

    public boolean updateRatingMongo(Rating rating){
        BasicDBObject query = new BasicDBObject();
        query.append("uid", rating.getUid());
        query.append("mid", rating.getMid());

        getRatingCollection().update(query,
                new BasicDBObject("$set", new BasicDBObject("score",rating.getScore()))
        );
        return true;
    }

    public static void main(String[] args) throws UnknownHostException {
        RatingService ratingService = new RatingService();
        // valerie: uid=231141674, mongodb自动插入时间戳
        List<Rating> ratingList = ratingService.findRatingByUID(6);
        HashMap<Integer,List<Rating>> map = new HashMap<>();
        for(Rating rating: ratingList){
            map.put(rating.getMid(), ratingList);
            System.out.println(String.format("%s, %s, %s, %s", rating.getUid(), rating.getMid()
                                                            , rating.getScore(), rating.getTimestamp()));

        }
        System.out.println(map.size());
        System.out.println(map.keySet().toString());

    }

}
