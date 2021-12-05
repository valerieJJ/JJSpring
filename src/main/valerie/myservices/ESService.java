package valerie.myservices;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import valerie.myModel.DTO.MovieDTO;
import valerie.myModel.Movie;
import valerie.myModel.Song;
import valerie.myModel.VO.MovieVO;
import valerie.myUtils.Constant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 7.8.0
@Service
public class ESService {
    @Autowired
    private RestHighLevelClient esClient;// = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost",9200,"http")));

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
//        IndexRequest request = new IndexRequest();
//
//    }

    public String search(String collection) throws IOException {
        GetIndexRequest request = new GetIndexRequest(collection);
        GetIndexResponse response = this.esClient.indices().get(request,RequestOptions.DEFAULT);
//        System.out.println(response.getMappings());
        return response.getMappings().toString();
    }

    public Map<String, Object> getDoc(String index_name, String id) throws IOException {
        GetRequest request = new GetRequest();
        request.index(index_name).id(id);
        GetResponse response = esClient.get(request, RequestOptions.DEFAULT);
        System.out.println(response.getSourceAsString());
        Map<String, Object> doc = response.getSourceAsMap();
        return doc;
    }

    public HashMap<List<MovieDTO>, String> fullQuery(String type, String index_name, String field, String value, String[] excludes, String[] includes, int maxItems) throws IOException {
//        MultiMatchQueryBuilder query = QueryBuilders.multiMatchQuery(text, "name", "descri");
        SearchRequest request = new SearchRequest();
        request.indices(index_name);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
//
//        if(type=="wildCard"){
//            boolQueryBuilder.should(QueryBuilders.wildcardQuery(field, value));
//        }else if(type=="term"){
//            boolQueryBuilder.filter(QueryBuilders.termQuery(field, value));
//        }else {
//            if(field=="name"){
//                System.out.println("...");
//                boolQueryBuilder.should(QueryBuilders.matchQuery(field, value).operator(Operator.AND).fuzziness(Fuzziness.AUTO));
////                boolQueryBuilder.filter(QueryBuilders.matchQuery(field, value).operator(Operator.AND));
//            }else{
//                boolQueryBuilder.filter(QueryBuilders.matchQuery(field, value));
//            }
//        }

        boolQueryBuilder.should(QueryBuilders.matchQuery(field, value).operator(Operator.AND).fuzziness(Fuzziness.AUTO));

        builder.query(boolQueryBuilder);
        builder.fetchSource(includes, excludes);
        builder.from(0);
        builder.size(maxItems);
        request.source(builder);
        SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        System.out.println("\n\n\nfull = "+hits.getTotalHits());
        for (SearchHit hit: hits){
            System.out.println(hit.getSourceAsString());
//            recommendations.add(new MovieVO((int) hit.getSourceAsMap().get("mid"), String.valueOf(hit.getScore())));
        }
        List<MovieDTO> recommendations = parseESResponse(response, includes);
        HashMap<List<MovieDTO>, String> map = new HashMap<>();
        map.put(recommendations, hits.getTotalHits().toString());
        return map;
    }

    public HashMap<List<MovieDTO>, String> fuzzyQuery(String type, String index_name, String field, String value, String[] excludes, String[] includes, int maxItems) throws IOException {
        // 纠错查询
        SearchRequest request = new SearchRequest();
        request.indices(index_name);
        SearchSourceBuilder builder = new SearchSourceBuilder();
//        builder.query(QueryBuilders.fuzzyQuery(field, value).fuzziness(Fuzziness.AUTO));

        builder.query(QueryBuilders.matchQuery(field, value).operator(Operator.AND).fuzziness(Fuzziness.AUTO));
//        builder.query(QueryBuilders.fuzzyQuery(field, value).fuzziness(Fuzziness.AUTO));
        builder.fetchSource(includes, excludes);
        builder.from(0);
        builder.size(10);
        request.source(builder);
        SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        System.out.println("\n\n\nfuzzy = "+hits.getTotalHits());
        List<MovieDTO> recommendations = parseESResponse(response, includes);
        HashMap<List<MovieDTO>, String> map = new HashMap<>();
        map.put(recommendations, hits.getTotalHits().toString());
        return map;
    }

    private List<MovieDTO> parseESResponse(SearchResponse response, String[] includes) {
        List<MovieDTO> recommendations = new ArrayList<>();
        for (SearchHit hit : response.getHits()) {
            MovieDTO movieDTO = new MovieDTO();
            movieDTO.setMid((int)hit.getSourceAsMap().get("mid"));
            recommendations.add(movieDTO);
        }
        return recommendations;
    }


    public static void main(String[] args) throws IOException {
        ESService es = new ESService();
        String collection = "movietags";

//        System.out.println(es.search(collection));
        Map<String, Object> doc = es.getDoc(collection, "2996");
//        List<MovieVO> lst = es.searchFullDoc(collection, "_id", "2996", 10);
//        List<MovieVO> lst = es.searchFullDoc(collection, "shoot","1996", 10);
//        List<MovieVO> lst = es.searchFullDoc(collection, "genres","Crime|Drama", 10);
//        List<MovieVO> lst2 = es.fuzzyQueries(collection, "genres","Drama");


        String[] excludes = {};
        String[] includes = {"mid", "name", "genres", "language"};

//
//        HashMap<List<MovieDTO>, String> map = es.fullQuery("match", collection, "genres","Thriller", excludes, includes, 10);
//        HashMap<List<MovieDTO>, String> map1 = es.fuzzyQuery(collection, excludes, includes,"name","Titanic");
//        HashMap<List<MovieDTO>, String> map2 = es.fullQuery("match", collection, "name","(1997)", excludes, includes, 10);
//        HashMap<List<MovieDTO>, String> map3 = es.fullQuery("match", collection, "language","English", excludes, includes, 10);
//        HashMap<List<MovieDTO>, String> map4 = es.fullQuery("match", collection, "language","Italiano|English", excludes, includes, 10);
//        HashMap<List<MovieDTO>, String> map5 = es.fullQuery("match", collection, "language","Italiano", excludes, includes, 10);
        HashMap<List<MovieDTO>, String> map6 = es.fullQuery("match", collection,"name", "Godfather: Part III", excludes, includes, 10);

//        System.out.println("3 : "+map3.values()); // 3 : [2494 hits]
//        System.out.println("4 : "+map4.values()); // 4 : [2523 hits]
//        System.out.println("5 : "+map5.values());
        System.out.println("6 : "+map6.values());
        return;
    }
}
