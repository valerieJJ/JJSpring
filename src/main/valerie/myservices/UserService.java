package valerie.myservices;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.*;
import org.bson.json.JsonWriterSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import valerie.myModel.Movie;
import valerie.myModel.User;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;
import com.google.gson.Gson;
import com.mongodb.util.JSON;
import org.bson.Document;
import valerie.myModel.requests.LoginUserRequest;
import valerie.myModel.requests.RegisterUserRequest;
import valerie.myUtils.CookieUtil;

import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Service
public class UserService {
    @Autowired
    private MongoClient mongoClient;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MongodbService mongodbService;
    @Autowired
    private RedisTemplate redisTemplate;

//    private DBCollection collection;
//    private DBObject userobj;

    private ThreadLocal<DBCollection> collection = new ThreadLocal<>();
    private ThreadLocal<DBObject> userobj = new ThreadLocal<>();

    public UserService() throws UnknownHostException {
    }

    public String getnickname(){
        User user = new User();
        return user.getNickname();
    }

    public User getUserbyCookie(String userTicket, HttpServletRequest request, HttpServletResponse response){
        if(!StringUtils.hasText(userTicket)) return  null;
        User user = (User) redisTemplate.opsForValue().get("user:"+userTicket);
        if (user!=null){
            CookieUtil.setCookie(request, response, "userticket", userTicket);
        }

        return user;
    }

    public User getDefaultUser(){
        User user = new User();
        user.setUsername("mickey");
        return user;
    }

    public User getUser(RegisterUserRequest request){
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        return user;
    }

    public DBCollection getUserCollection(){
        DB db = mongoClient.getDB( "MovieDB" );
        Set<String> collections = db.getCollectionNames();
        // 先检查数据库中有没有 user 表
        System.out.println("Contain collections: "+collections);
        if(!collections.contains("Users")){
            System.out.println("Initializing a User collection!");
            // 将User表创建为 capped collection，本质上是一个固定大小的集合，并且仅允许插入。
            db.createCollection("Users", new BasicDBObject("capped", true)
                    .append("size", 1048576));

        }
        DBCollection usercollection = db.getCollection("Users");
        return usercollection;
    }

    public List<Movie> getCollectionData(String field, String value) throws UnknownHostException {
        DB db = mongoClient.getDB( "MovieDB" );
        DBCollection dbcollection = db.getCollection("Movie");
        collection.set(dbcollection);
        DBObject myDoc = collection.get().findOne();
        System.out.println(myDoc);

//        DBObject res = mongodbService.getDataObj();
        List<Movie> res = mongodbService.getDataObj(field,value);
        return res;
    }

    public User registerUser(RegisterUserRequest request){
        // 先检查user表中是否已存在同名用户
        if(checkUserExist(request.getUsername())){
            return null;
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFirst(true);
        user.setTimestamp(System.currentTimeMillis());
        BasicDBObject u = new BasicDBObject("username", user.getUsername())
                .append("password", user.getPassword());
        getUserCollection().insert(u);
//      getUserCollection().insert(Document.parse(objectMapper.writeValueAsString(user)));
        return user;
    }

    public User loginUser(LoginUserRequest loginUserRequest, HttpServletRequest request, HttpServletResponse response){
        User user = findUserMongoDB(loginUserRequest.getUsername());
        if(null == user) {
            return null;
        }else if(!user.passwordMatch(loginUserRequest.getPassword())){
            return null;
        }
        String userticket = loginUserRequest.getUsername()+user.getPassword();
        CookieUtil.setCookie(request, response, "userticket", userticket);
        return user;
    }

    private User DBObject2User(DBObject object){
        try{
            return objectMapper.readValue(JSON.serialize(object), User.class);
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

    public boolean checkUserExist(String username){
        return null != findByUsername(username);
    }

    public User findByUsername(String username){
        BasicDBObject query = new BasicDBObject("username",username);
        DBObject obj = getUserCollection().findOne(query);
        userobj.set(obj);
        System.out.println("check user exist");

        if(null == userobj.get())
            return null;
        return DBObject2User(userobj.get());
    }

    public User findUserMongoDB(String name){
        BasicDBObject query = new BasicDBObject("username", name);
        DBCollection coll = getUserCollection();
//        List<DBObject> userObjList = coll.find(query);
        DBObject obj = coll.findOne(query);
        userobj.set(obj);
        if(null == userobj)
            return null;
        System.out.println("Get user collection!"+userobj.toString());
        User user = DBObject2User(userobj.get());

        return user;
    }

//    public boolean updateUser(User user){
//        getUserCollection().updateOne(Filters.eq("uid", user.getUid()), new Document().append("$set",new Document("first", user.isFirst())));
//        getUserCollection().updateOne(Filters.eq("uid", user.getUid()), new Document().append("$set",new Document("prefGenres", user.getPrefGenres())));
//        return true;
//    }
//
//    public User findByUID(int uid){
//        Document user = getUserCollection().find(new Document("uid",uid)).first();
//        if(null == user || user.isEmpty())
//            return null;
//        return documentToUser(user);
//    }
//
//    public void removeUser(String username){
//        getUserCollection().deleteOne(new Document("username",username));
//    }

}
