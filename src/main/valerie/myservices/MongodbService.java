package valerie.myservices;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.*;
import com.mongodb.client.model.Filters;
import com.mongodb.util.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import valerie.myModel.Movie;
import valerie.myModel.User;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

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
        int cnt = 0;
        while(cursor.hasNext() && cnt<=3){
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

}