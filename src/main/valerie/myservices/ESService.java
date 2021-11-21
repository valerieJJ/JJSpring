package valerie.myservices;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import valerie.mycontrollers.Song;

import java.io.IOException;

// 7.8.0
@Service
public class ESService {
    @Autowired
    private RestHighLevelClient esClient ;//= new RestHighLevelClient(RestClient.builder(new HttpHost("localhost",9200,"http")));


    public void createIndex(String index) throws IOException {
        // 创建索引 "musicdb"
        CreateIndexRequest request = new CreateIndexRequest(index);
        CreateIndexResponse createIndexResponse = esClient.indices().create(request, RequestOptions.DEFAULT);
        // 响应状态
        boolean acknowledged = createIndexResponse.isAcknowledged();
        System.out.println("Index: "+acknowledged);

    }
    public void write() throws IOException {
        IndexRequest request = new IndexRequest();
        request.index("musicdb").id("1001");

        Song song = new Song();
        song.setName("BilleJean");
        song.setLanguage("English");
        song.setLikes(999);

        ObjectMapper mapper = new ObjectMapper();
        String songJson = mapper.writeValueAsString(song);
        request.source(songJson, XContentType.JSON);

        IndexResponse response = this.esClient.index(request, RequestOptions.DEFAULT);
        this.esClient.close();
    }

//    public void writeMovieDB() throws UnknownHostException {
//        MongodbService mongodbService = new MongodbService();
//        DBObject data = mongodbService.getDataObj();
//
//        IndexRequest request = new IndexRequest();
//
//    }

    public void search(String collection) throws IOException {
        GetIndexRequest request = new GetIndexRequest(collection);
        GetIndexResponse response = this.esClient.indices().get(request,RequestOptions.DEFAULT);
        System.out.println(response.getMappings());
    }

    public static void main(String[] args) throws IOException {
        ESService es = new ESService();
        String collection = "movietags";
        es.search(collection);


    }
}
