package valerie.myservices;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.*;
import com.mongodb.util.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import valerie.myModel.Favorite;
import valerie.myModel.Movie;
import valerie.myModel.Rating;
import valerie.myModel.Tag;
import valerie.myModel.requests.FavoriteRequest;
import valerie.myModel.requests.TagRequest;

import java.lang.reflect.Field;
import java.net.UnknownHostException;
import java.util.*;

@Service
public class FavoriteService {

//    private MongoClient mongoClient = new MongoClient( "localhost", 27017);

    @Autowired
    private MongodbService mongodbService;
    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private Jedis jedis;

    private String collectionName = "Favorite";

    public FavoriteService() throws UnknownHostException {
    }

    public Favorite DBOjbect2Favorite(DBObject object){
        try{
            return objectMapper.readValue(JSON.serialize(object), Favorite.class);

        } catch (JsonMappingException e) {
            e.printStackTrace();
            return null;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

//    public DBCollection getCollection(String collectionName){
//        DB db = mongoClient.getDB("MovieDB");
//        Set<String> names = db.getCollectionNames();
//        DBCollection collection;
//        if(names.contains(collectionName)){
//            collection = db.getCollection(collectionName);
//        }else{
//            System.out.println("create and get a new collection: "+collectionName);
//            collection = db.getCollection(collectionName);
//        }
//        return collection;
//    }

    public DBCollection getCollection(){
        DBCollection collection = mongodbService.getCollection(this.collectionName);
        return collection;
    }

    public boolean insertFavorite2Mongo(Favorite favorite) throws IllegalAccessException {
        DBObject dbObject = mongodbService.bean2DBObject(favorite);
        this.getCollection().insert(dbObject);
        return true;
    }

    public boolean dropFavorite2Mongo(Integer uid, Integer mid) throws IllegalAccessException {
        BasicDBObject query = new BasicDBObject();
        query.append("uid", uid);
        query.append("mid", mid);

        this.getCollection().findAndRemove(query);
        return true;
    }

    public Favorite findFavorite(int uid, int mid){
        BasicDBObject query = new BasicDBObject();
        query.append("uid", uid);
        query.append("mid", mid);
        DBCursor cursor = this.getCollection().find(query);
        if (cursor.count()>0){
            System.out.println("It's in your favorites");
             Favorite favorite = DBOjbect2Favorite(cursor.next());
            return favorite;
        }else {
            return null;
        }
    }

    public List<Favorite> getFavoriteHistory(int uid){
        BasicDBObject query = new BasicDBObject();
        query.append("uid", uid);
        DBCursor cursor = this.getCollection().find(query);
        List<Favorite> favoriteList = new ArrayList<>();
        while(cursor.hasNext()){
            Favorite favorite = DBOjbect2Favorite(cursor.next());
            favoriteList.add(favorite);
        }
        return favoriteList;
    }

    private void updateFavorite2Redis(Favorite favorite){
        // 刷新缓存时，先删缓存再写缓存
        if(jedis.exists("uid:"+favorite.getUid())
                && jedis.llen("uid:"+favorite.getUid())>=40){
            jedis.rpop("uid:"+favorite.getUid());
        }
        jedis.lpush("uid:"+favorite.getUid(),String.valueOf(favorite.getMid()));
    }


    private void dropFavorite2Redis(Favorite favorite){
        // 刷新缓存时，先删缓存再写缓存
        if(jedis.exists("uid:"+favorite.getUid())
                && jedis.llen("uid:"+favorite.getUid())>=40){
            jedis.rpop("uid:"+favorite.getUid());
        }
        jedis.lpush("uid:"+favorite.getUid(),String.valueOf(favorite.getMid()));
    }

    public boolean favoriteExistMongo(int uid, int mid){
        if(findFavorite(uid, mid)!=null){
            return true;
        }else {
            return false;
        }
    }

    public boolean updateFavorite(FavoriteRequest favoriteRequest) throws IllegalAccessException {
        Favorite favorite = new Favorite(favoriteRequest.getUid(), favoriteRequest.getMid());
        updateFavorite2Redis(favorite); // 先更新redis缓存
        System.out.print("Already updated favorites in redis...");
        if(favoriteExistMongo(favorite.getUid(), favorite.getMid())){ //再操作数据库，如果以前收藏过该电影，
            return updateFavorite2Mongo(favorite); //
        }else{
            return insertFavorite2Mongo(favorite); // 否则插入新的收藏
        }
    }

    public boolean dropFavorite(FavoriteRequest favoriteRequest) throws IllegalAccessException {
        Favorite favorite = new Favorite(favoriteRequest.getUid(), favoriteRequest.getMid());
        dropFavorite2Redis(favorite); // 先更新redis缓存
        if(favoriteExistMongo(favoriteRequest.getUid(), favoriteRequest.getMid())){ //再操作数据库，如果以前收藏过该电影，
            return dropFavorite2Mongo(favoriteRequest.getUid(), favoriteRequest.getMid()); //
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

    public boolean updateFavorite2Mongo(Favorite favorite){//先查询后重置用户对该电影的评价
        BasicDBObject query = new BasicDBObject();
        query.append("uid", favorite.getUid());
        query.append("mid", favorite.getMid());

        getCollection().update(query,
                new BasicDBObject("$set",  // {$set:{field:value}}把文档中某个字段field的值设为value
                        new BasicDBObject("timestamp",favorite.getTimestamp())
                )
        );
        return true;
    }

    public static void main(String[] args) throws UnknownHostException, IllegalAccessException {
        FavoriteService favoriteService = new FavoriteService();
//        DBCollection collection = favoriteService.getCollection("Favorite");
        Favorite favorite = new Favorite(231141674, 1721);
        FavoriteRequest favoriteRequest = new FavoriteRequest(231141674, 1721);
//        DBObject object = favoriteService.bean2DBObject(favorite);
//        favoriteService.updateFavorite2Mongo(favorite);
        favoriteService.dropFavorite(favoriteRequest);
//        favoriteService.getCollection().insert(object);

        List<Favorite> favoriteList = favoriteService.getFavoriteHistory(231141674);
        System.out.println("favoriteList.size() = "+favoriteList.size());
        HashMap<Integer,List<Favorite>> map = new HashMap<>();
        for(Favorite favorite1: favoriteList){
            map.put(favorite1.getMid(), favoriteList);
            System.out.println(String.format("%s, %s, %s", favorite1.getUid(), favorite1.getMid()
                    , favorite1.getTimestamp()));

        }
        System.out.println(map.size());
        System.out.println(map.keySet().toString());
    }

}
