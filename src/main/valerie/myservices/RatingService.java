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
import valerie.myModel.Movie;
import valerie.myModel.Rating;
import valerie.myModel.requests.MovieRatingRequest;

import java.io.IOException;
import java.util.Set;

@Service
public class RatingService {
    @Autowired
    private MongoClient mongoClient;
    @Autowired
    private MongodbService mongodbService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private Jedis jedis;

    private DBCollection ratingCollection;

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
        if(null == ratingCollection){
            DB db = mongoClient.getDB( "MovieDB" );
            Set<String> collections = db.getCollectionNames();
            ratingCollection = db.getCollection("Rating");
        }
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

    public boolean ratingExistMongo(int uid, int mid){
        if(findRating(uid, mid)!=null){
            return true;
        }else {
            return false;
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

}
