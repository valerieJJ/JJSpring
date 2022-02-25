package valerie.myservices;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.*;
import com.mongodb.client.model.Filters;
import com.mongodb.util.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import valerie.myModel.Movie;
import valerie.myModel.User;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

// To directly connect to a single MongoDB server (note that this will not auto-discover the primary even
// if it's a member of a replica set:
@Service
public class MongodbService {
    @Autowired
    private MongoClient mongoClient;
    @Autowired
    private ObjectMapper objectMapper;

    public MongodbService() throws UnknownHostException {
    }


    public String getData() throws UnknownHostException {
        DB db = mongoClient.getDB( "MovieDB" );
        DBCollection coll = db.getCollection("Movie");

        DBObject myDoc = coll.findOne();
        System.out.println(myDoc);
        return myDoc.toString();
    }

    public DBCollection getCollection(String collectionName){
        DB db = mongoClient.getDB("MovieDB");
        Set<String> names = db.getCollectionNames();
        DBCollection coll;
        if(names.contains(collectionName)){
            coll = db.getCollection(collectionName);
        }else{
            System.out.println("create and get a new collection");
            coll = db.getCollection(collectionName);
        }

        return coll;
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

    public List<Movie> getDataObj(String field, String value) throws UnknownHostException {
        DB db = mongoClient.getDB( "MovieDB" );
        DBCollection coll = db.getCollection("Movie");
        DBObject query = new BasicDBObject(field,value);

        DBCursor cursor = coll.find(query);
        List<Movie> movies = new ArrayList<>();
        int cnt = 1;
        while(cursor.hasNext() && cnt<=9){
            DBObject movieObj = cursor.next();
            Movie movie = this.DBObject2Movie(movieObj);
            movies.add(movie);
            cnt++;
        }

        System.out.println(movies);
        return movies;
    }

    public void insert(DBCollection coll) {
        BasicDBObject doc = new BasicDBObject("name", "MongoDB")
                .append("type", "database")
                .append("count", 1)
                .append("info", new BasicDBObject("x", 203).append("y", 102));
        coll.insert(doc);
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


}