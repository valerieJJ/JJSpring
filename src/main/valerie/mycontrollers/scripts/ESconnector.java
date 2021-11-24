package valerie.mycontrollers.scripts;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClient;
import org.apache.http.HttpHost;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import valerie.mycontrollers.Song;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

//7.8.0
public class ESconnector {
    public static void main(String[] args) throws IOException {
        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost",9200,"http"))
        );

        // creating a new index in elasticsearch
//        CreateIndexRequest request = new CreateIndexRequest("music");
//        CreateIndexResponse response = esClient.indices().create(request, RequestOptions.DEFAULT);
//        response.isAcknowledged()

        IndexRequest request = new IndexRequest();
        request.index("musicdb").id("1001");

        Song song = new Song();
        song.setName("BilleJean");
        song.setLanguage("English");
        song.setLikes(999);

        ObjectMapper mapper = new ObjectMapper();
        String songJson = mapper.writeValueAsString(song);
        request.source(songJson, XContentType.JSON);

        IndexResponse response = esClient.index(request, RequestOptions.DEFAULT);
        esClient.close();
        System.out.println("Index create successfully");
//        boolean acknowledged = response.

    }

}
