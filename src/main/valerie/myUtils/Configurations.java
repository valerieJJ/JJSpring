package valerie.myUtils;

import com.mongodb.MongoClient;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
//import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class Configurations {

    private String mongoHost = "localhost";
    private int mongoPort = 27017;
    private String esClusterName = "jj-cluster";
    private String esHost = "localhost";
    private int esPort =  9300;


    @Bean(name = "mongoClient")
    public MongoClient getMongoClient() throws UnknownHostException {
        MongoClient mongoClient = new MongoClient( mongoHost , mongoPort );
        return mongoClient;
    }

    @Bean(name = "esClient")
    public RestHighLevelClient getESClient() throws UnknownHostException {
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost",9200,"http"))
        );
        return esClient;
    }

//    @Bean(name = "transportClient")
//    public TransportClient getTransportClient() throws UnknownHostException {
//        Settings settings = Settings.builder().put("cluster.name",esClusterName).build();
//        TransportClient esClient = new PreBuiltTransportClient(settings);
//        esClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(esHost), esPort));
//        return esClient;
//    }
}
