package valerie.myservices;

import com.mongodb.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.UnknownHostException;

// To directly connect to a single MongoDB server (note that this will not auto-discover the primary even
// if it's a member of a replica set:
@Service
public class MongodbService {
    @Autowired
    private MongoClient mongoClient;

    public MongodbService() throws UnknownHostException {
    }

    public String getData() throws UnknownHostException {
        DB db = mongoClient.getDB( "MovieDB" );
        DBCollection coll = db.getCollection("Movie");

        DBObject myDoc = coll.findOne();
        System.out.println(myDoc);
        return myDoc.toString();
    }

    public DBObject getDataObj() throws UnknownHostException {
        DB db = mongoClient.getDB( "MovieDB" );
        DBCollection coll = db.getCollection("Movie");

        DBObject myDoc = coll.findOne();
        System.out.println(myDoc);
        return myDoc;
    }

    public void insert(DBCollection coll) {
        BasicDBObject doc = new BasicDBObject("name", "MongoDB")
                .append("type", "database")
                .append("count", 1)
                .append("info", new BasicDBObject("x", 203).append("y", 102));
        coll.insert(doc);
    }

}