package valerie.mycontrollers.scripts;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ESService {
    // for elasticsearch version-5.6.2
    private String elasticIp = "localhost";
    private int elasticPort = 9200;
    private String esClusterName = "jj-cluster";
    private String esHost = "localhost";
    private int esPort =  9300;

    private TransportClient esClient;

    public TransportClient getTransportClient() throws UnknownHostException {
        // 设置集群名称
        Settings settings = Settings.builder().put("cluster.name",this.esClusterName).build();
        // 创建client
        TransportClient esClient = new PreBuiltTransportClient(settings);
        esClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(this.esHost), this.esPort));
        return esClient;
    }


    public void esConnection() throws UnknownHostException {
        TransportClient esCLient = getTransportClient();
        // 全文检索
        MultiMatchQueryBuilder myQuery = QueryBuilders.multiMatchQuery("English","language");
        SearchResponse sr = esCLient.prepareSearch().setIndices("movietags")
                .setQuery(myQuery)
                .setSize(10).execute().actionGet();
        SearchHits hits=sr.getHits();
        System.out.println("Searching in ES:");
        for(SearchHit hit : hits){
            System.out.println(hit.getSourceAsString()+"\n");
        }
        // 搜索数据
        //        GetResponse response = esClient.prepareGet("movietags")

    }

    public static void main(String[] args) throws UnknownHostException {
        //      HttpHost host = new HttpHost( "localhost", 9200,"http");
        ESController es = new ESController();
        es.esConnection();

    }
}