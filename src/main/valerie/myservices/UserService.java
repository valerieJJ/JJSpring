package valerie.myservices;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.*;
import org.bson.json.JsonWriterSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import valerie.myModel.User;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Set;
import com.google.gson.Gson;
import com.mongodb.util.JSON;
import org.bson.Document;
import valerie.myModel.requests.LoginUserRequest;
import valerie.myModel.requests.RegisterUserRequest;

import javax.jws.soap.SOAPBinding;


@Service
public class UserService {
    @Autowired
    private MongoClient mongoClient;
    @Autowired
    private ObjectMapper objectMapper;

    private DBCollection collection;
    private DBObject userobj;

    public UserService() throws UnknownHostException {
    }

    public String getnickname(){
        User user = new User();
        return user.getNickname();
    }

    public User getDefaultUser(){
        User user = new User();
        user.setUsername("michael");
        user.setPassword("valerie");
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

    public DBObject getCollectionData(){
        DB db = mongoClient.getDB( "MovieDB" );
        collection = db.getCollection("Movie");

        DBObject myDoc = collection.findOne();
        System.out.println(myDoc);
        return myDoc;
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

    public User loginUser(LoginUserRequest request){
        User user = findUser(request.getUsername());
        if(null == user) {
            return null;
        }else if(!user.passwordMatch(request.getPassword())){
            return null;
        }
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

    public DBObject findByUsername(String username){
        BasicDBObject query = new BasicDBObject("username",username);
        userobj = getUserCollection().findOne(query);
        System.out.println("check user exist");

        if(null == userobj)
            return null;
        return userobj;
    }

    public User findUser(String name){
        BasicDBObject query = new BasicDBObject("username", name);
        DBCollection coll = getUserCollection();
        userobj = coll.findOne(query);
        if(null == userobj)
            return null;
        System.out.println("Get user collection!"+userobj.toString());
        User user = DBObject2User(userobj);

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
