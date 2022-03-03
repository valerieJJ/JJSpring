package valerie.myservices;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import valerie.myModel.Favorite;
import valerie.myModel.Like;
import valerie.myModel.requests.FavoriteRequest;
import valerie.myModel.requests.LikeRequest;

import java.lang.reflect.Field;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class LikeService {

    @Autowired
    private MongodbService mongodbService;
    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private Jedis jedis;

    private String collectionName = "Likes";

    public LikeService() throws UnknownHostException {
    }

    public Like DBOjbect2Like(DBObject object){
        try{
            return objectMapper.readValue(JSON.serialize(object), Like.class);

        } catch (JsonMappingException e) {
            e.printStackTrace();
            return null;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public DBCollection getCollection(){
        DBCollection collection = mongodbService.getCollection(this.collectionName);
        return collection;
    }

    public boolean insertLike2Mongo(Like like) throws IllegalAccessException {
        DBObject dbObject = mongodbService.bean2DBObject(like);
        this.getCollection().insert(dbObject);
        return true;
    }

    public boolean dropLike2Mongo(Integer uid, Integer mid) throws IllegalAccessException {
        BasicDBObject query = new BasicDBObject();
        query.append("uid", uid);
        query.append("mid", mid);

        this.getCollection().findAndRemove(query);
        return true;
    }

    public Like findLike(int uid, int mid){
        BasicDBObject query = new BasicDBObject();
        query.append("uid", uid);
        query.append("mid", mid);
        DBCursor cursor = this.getCollection().find(query);
        if (cursor.count()>0){
            System.out.println("It's in your likes list!");
            Like like = DBOjbect2Like(cursor.next());
            return like;
        }else {
            return null;
        }
    }

    public List<Like> getMovieLikes(int mid){
        BasicDBObject query = new BasicDBObject();
        query.append("mid", mid);
        DBCursor cursor = this.getCollection().find(query);
        List<Like> likeList = new ArrayList<>();
        while(cursor.hasNext()){
            Like like  = DBOjbect2Like(cursor.next());
            likeList.add(like);
        }
        return likeList;
    }

    public List<Like> getUserLikes(int uid){
        BasicDBObject query = new BasicDBObject();
        query.append("uid", uid);
        DBCursor cursor = this.getCollection().find(query);
        List<Like> likeList = new ArrayList<>();
        while(cursor.hasNext()){
            Like like = DBOjbect2Like(cursor.next());
            likeList.add(like);
        }
        return likeList;
    }

    private void updateLike2Redis(Like like){
        // 刷新缓存时，先删缓存再写缓存
        if(jedis.exists("uid:"+like.getUid())
                && jedis.llen("uid:"+like.getUid())>=40){
            jedis.rpop("uid:"+like.getUid());
        }
        jedis.lpush("uid:"+like.getUid(),String.valueOf(like.getMid()));
    }

    private void dropLike2Redis(Like like){
        // 刷新缓存时，先删缓存再写缓存
        if(jedis.exists("uid:"+like.getUid())
                && jedis.llen("uid:"+like.getUid())>=40){
            jedis.rpop("uid:"+like.getUid());
        }
        jedis.lpush("uid:"+like.getUid(),String.valueOf(like.getMid()));
    }

    public boolean likeExistMongo(int uid, int mid){
        if(findLike(uid, mid)!=null){
            return true;
        }else {
            return false;
        }
    }

    public boolean updateLike(LikeRequest likeRequest) throws IllegalAccessException {
        Like like = new Like(likeRequest.getUid(), likeRequest.getMid());
        updateLike2Redis(like); // 先更新redis缓存
        if(likeExistMongo(like.getUid(), like.getMid())){ //再操作数据库，如果以前收藏过该电影，
            return updateLike2Mongo(like); //
        }else{
            return insertLike2Mongo(like); // 否则插入新的收藏
        }
    }

    public boolean dropLike(LikeRequest likeRequest) throws IllegalAccessException {
        Like like = new Like(likeRequest.getUid(), likeRequest.getMid());
        dropLike2Redis(like); // 先更新redis缓存
        if(likeExistMongo(likeRequest.getUid(), likeRequest.getMid())){ //再操作数据库，如果以前收藏过该电影，
            return dropLike2Mongo(likeRequest.getUid(), likeRequest.getMid()); //
        }else{
            return false;
        }
    }

    public <T> DBObject bean2DBObject(T bean) throws IllegalArgumentException,
            IllegalAccessException {
        if (bean == null) {
            return null;
        }
        DBObject dbObject = new BasicDBObject();
        // 获取对象对应类中的所有属性域
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            // 获取属性名
            String varName = field.getName();
            // 修改访问控制权限
            boolean accessFlag = field.isAccessible();
            if (!accessFlag) {
                field.setAccessible(true);
            }
            Object param = field.get(bean);
            if (param == null) {
                continue;
            } else if (param instanceof Integer) {//判断变量的类型
                int value = ((Integer) param).intValue();
                dbObject.put(varName, value);
            } else if (param instanceof String) {
                String value = (String) param;
                dbObject.put(varName, value);
            } else if (param instanceof Double) {
                double value = ((Double) param).doubleValue();
                dbObject.put(varName, value);
            } else if (param instanceof Float) {
                float value = ((Float) param).floatValue();
                dbObject.put(varName, value);
            } else if (param instanceof Long) {
                long value = ((Long) param).longValue();
                dbObject.put(varName, value);
            } else if (param instanceof Boolean) {
                boolean value = ((Boolean) param).booleanValue();
                dbObject.put(varName, value);
            } else if (param instanceof Date) {
                Date value = (Date) param;
                dbObject.put(varName, value);
            }
            // 恢复访问控制权限
            field.setAccessible(accessFlag);
        }
        return dbObject;
    }

    public boolean updateLike2Mongo(Like like){//先查询后重置用户对该电影的评价
        BasicDBObject query = new BasicDBObject();
        query.append("uid", like.getUid());
        query.append("mid", like.getMid());

        getCollection().update(query,
                new BasicDBObject("$set",  // {$set:{field:value}}把文档中某个字段field的值设为value
                        new BasicDBObject("timestamp", like.getTimestamp())
                )
        );
        return true;
    }

}
