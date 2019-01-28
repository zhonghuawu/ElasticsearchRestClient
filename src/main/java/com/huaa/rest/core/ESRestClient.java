package com.huaa.rest.core;

import com.huaa.util.GsonUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.*;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.Arrays;

/**
 * Desc:
 *
 * @author Huaa
 * @date 2018/12/8 23:49
 */

@Log4j2
public class ESRestClient {

    private static String DEFAULT_TYPE = "_doc";
    private RestClient lowLevelClient;
    private RestHighLevelClient client;

    public ESRestClient(String... ips) {
        HttpHost[] hosts = Arrays.stream(ips)
                .map(ip -> ip.split(":"))
                .filter(address -> address.length >= 2)
                .map(address -> new HttpHost(address[0], Integer.parseInt(address[1])))
                .peek(log::info)
                .toArray(HttpHost[]::new);

        client = new RestHighLevelClient(RestClient.builder(hosts));
        lowLevelClient = client.getLowLevelClient();
        log.info("build rest-high-level client succeed");
    }

    public RestHighLevelClient getClient() {
        return client;
    }

    public void close() throws IOException {
        try {
            if (client != null) {
                client.close();
            }
        } catch (IOException e) {
            log.error("", e);
        }
    }

    public boolean isExistedTemplate(String templateName) throws IOException {
        Request request = new Request("GET", "/_template/" + templateName);
        Response response = lowLevelClient.performRequest(request);
        return response.getStatusLine().getStatusCode() == 200;
    }

    public boolean putTemplate(String templateName, XContentBuilder templateBuilder) throws IOException {
        PutIndexTemplateRequest request = new PutIndexTemplateRequest()
                .name(templateName)
                .source(templateBuilder);
        AcknowledgedResponse response = client.indices().putTemplate(request, RequestOptions.DEFAULT);
        return response.isAcknowledged();
    }

    public boolean index(String indexName, Object... objects) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        for (Object object : objects) {
            bulkRequest.add(new IndexRequest(indexName, DEFAULT_TYPE).source(GsonUtil.toJson(object), XContentType.JSON));
        }
        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulkResponse.hasFailures();
    }

    public boolean delete(String indexName, String id) throws IOException {
        DeleteRequest request = new DeleteRequest(indexName, DEFAULT_TYPE, id);
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        return response.status() == RestStatus.OK;
    }

    public SearchResponse search(String indexName, QueryBuilder queryBuilder, int pageSize, int page) throws IOException {
        SearchSourceBuilder builder = new SearchSourceBuilder()
                .query(queryBuilder)
                .size(pageSize)
                .from((page - 1) * pageSize);
        SearchRequest request = new SearchRequest(indexName)
                .types(DEFAULT_TYPE)
                .source(builder);
        return client.search(request, RequestOptions.DEFAULT);
    }


}
