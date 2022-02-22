package valerie.myservices;

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
import valerie.myModel.Tag;
import valerie.myModel.requests.TagRequest;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Service
public class TagService {

    private MongoClient mongoClient = new MongoClient( "localhost", 27017);

    @Autowired
    private MongodbService mongodbService;
    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private Jedis jedis;

    public TagService() throws UnknownHostException {
    }

    public Tag DBOjbect2Tag(DBObject object){
        try{
            return objectMapper.readValue(JSON.serialize(object), Tag.class);

        } catch (JsonMappingException e) {
            e.printStackTrace();
            return null;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public DBCollection getCollection(String collectionName){

        DB db = mongoClient.getDB("MovieDB");

        Set<String> names = db.getCollectionNames();
        DBCollection collection;
//        String collectionName = "Tag";
        if(names.contains(collectionName)){
            collection = db.getCollection(collectionName);
        }else{
            System.out.println("create and get a new collection: "+collectionName);
            collection = db.getCollection(collectionName);
        }

//        DBCollection collection = mongodbService.getCollection("Tag");
        return collection;
    }

    public DB getDB(){
        return mongoClient.getDB("MovieDB");
    }

    public DBCollection getCollection(){

        DB db = mongoClient.getDB("MovieDB");
        Set<String> names = db.getCollectionNames();
        DBCollection collection;
        String collectionName = "Tag";
        if(names.contains(collectionName)){
            collection = db.getCollection(collectionName);
        }else{
            System.out.println("create and get a new collection");
            collection = db.getCollection(collectionName);
        }

//        DBCollection collection = mongodbService.getCollection("Tag");
        return collection;
    }

    public void insertNewTag2Mongo(Tag tag) throws IllegalAccessException {
        DBObject dbObject = mongodbService.bean2DBObject(tag);
        this.getCollection().insert(dbObject);
    }

    public Tag findTag(int uid, int mid){
        BasicDBObject query = new BasicDBObject();
        query.append("uid", uid);
        query.append("mid", mid);
        DBCursor cursor = this.getCollection().find(query);
        if (cursor.count()>0){
            System.out.println("you have written tag for it before");
            Tag tag = DBOjbect2Tag(cursor.next());
            return tag;
        }else {
            return null;
        }
    }

    public List<Tag> getTagHistory(int uid){
        BasicDBObject query = new BasicDBObject();
        query.append("uid", uid);
        DBCursor cursor = this.getCollection().find(query);
        List<Tag> tagHistory = new ArrayList<>();
        if(cursor.hasNext()){
            Tag tag = DBOjbect2Tag(cursor.next());
            tagHistory.add(tag);
        }
        return tagHistory;
    }

    private void updateTag2Redis(Tag tag){
        // 刷新缓存时，先删缓存再写缓存
        if(jedis.exists("uid:"+tag.getUid())
                && jedis.llen("uid:"+tag.getUid())>=40){
            jedis.rpop("uid:"+tag.getUid());
        }
        jedis.lpush("uid:"+tag.getUid(),tag.getMid()+":"+tag.getTag());
    }

    public boolean tagExistMongo(int uid, int mid){
        if(findTag(uid, mid)!=null){
            return true;
        }else {
            return false;
        }
    }

    public boolean updateTag(TagRequest tagRequest) throws IllegalAccessException {
        Tag tag = new Tag(tagRequest.getUid(), tagRequest.getMid(), tagRequest.getTag());
        updateTag2Redis(tag); // 先更新redis缓存
        if(tagExistMongo(tag.getUid(), tag.getMid())){ //再操作数据库，如果以前评论过该电影，
            return updateTag2Mongo(tag); // 就更新该评论
        }else{
            return insertTag2Mongo(tag); // 否则插入新的评论
        }
    }

    public boolean updateTag2Mongo(Tag tag){//先查询后重置用户对该电影的评价
        BasicDBObject query = new BasicDBObject();
        query.append("uid", tag.getUid());
        query.append("mid", tag.getMid());

        getCollection().update(query,
                new BasicDBObject("$set",  // {$set:{field:value}}把文档中某个字段field的值设为value
                                   new BasicDBObject("tag",tag.getTag())
                )
        );
        return true;
    }

    public boolean insertTag2Mongo(Tag tag) throws IllegalAccessException {
        DBObject dbObject = mongodbService.bean2DBObject(tag);
        this.getCollection().insert(dbObject);
        return true;
    }

    public static void main(String[] args) throws UnknownHostException {
        TagService tagService = new TagService();
        DBCollection collection = tagService.getCollection("Favorites");

//        DB
//        System.out.println(collection.getName());

//        List<Tag> tagList = tagService.getTagHistory(663);
//        HashMap<Integer,List<Tag>> map = new HashMap<>();
//        for(Tag tag: tagList){
//            map.put(tag.getMid(), tagList);
//            System.out.println(String.format("%s, %s, %s, %s", tag.getUid(), tag.getMid()
//                    , tag.getTag(), tag.getTimestamp()));
//
//        }
//        System.out.println(map.size());
//        System.out.println(map.keySet().toString());
    }

}
