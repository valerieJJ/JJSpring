package valerie.myUtils;

import com.mongodb.MongoClient;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.client.transport.TransportClient;
//import org.elasticsearch.common.settings.Settings;
//import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import redis.clients.jedis.Jedis;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Properties;

@Configuration
public class Configurations {

    private String mongoHost = "localhost";
    private int mongoPort = 27017;
    private String esClusterName = "jj-cluster";
    private String esHost = "localhost";
    private int esPort =  9300;
    private String redisHost;

    public Configurations() throws IOException {
        Properties properties = new Properties();
        Resource resource = new ClassPathResource("neverland.properties");
        properties.load(new FileInputStream(resource.getFile()));
        this.redisHost = properties.getProperty("redis.host");
    }

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

    @Bean(name = "jedis")
    public Jedis getRedisClient(){
        Jedis jedis = new Jedis(this.redisHost);
        return jedis;
    }

//    @Bean(name = "transportClient")
//    public TransportClient getTransportClient() throws UnknownHostException {
//        Settings settings = Settings.builder().put("cluster.name",esClusterName).build();
//        TransportClient esClient = new PreBuiltTransportClient(settings);
//        esClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(esHost), esPort));
//        return esClient;
//    }
}
