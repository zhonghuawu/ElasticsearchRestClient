package com.huaa.rest.clients;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.huaa.rest.core.ESBulkProcessor;
import com.huaa.rest.core.ESRestClient;
import com.huaa.rest.data.RawBlog;
import com.huaa.rest.data.TemplateUtil;
import com.huaa.util.GsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Desc:
 *
 * @author Huaa
 * @date 2018/12/9 1:01
 */

public class BlogRestClient {
    private static Logger logger = LogManager.getLogger(BlogRestClient.class);

    private ESRestClient client;
    private BulkProcessor bulkProcessor;

    private static String alias = "blog";

    public BlogRestClient(ESRestClient client) {
        this.client = client;
        String templateName = alias + "-template";
        try {
            XContentBuilder templateSource = TemplateUtil.blogTemplate(alias);
            boolean result = client.putTemplate(templateName, templateSource);
            if (! result)
            {
                logger.error("putTemplate failed, {}", templateName);
            }
        } catch (Throwable e) {
            logger.error("putTemplate failed, {}", templateName, e);
        }
        bulkProcessor = new ESBulkProcessor(client.getClient()).getBulkProcessor();
    }

    private static String joinIndexName(String suffix)
    {
        return Joiner.on("-").join(alias, suffix);
    }

    public boolean store(String suffix, List<RawBlog> rawBlogList) throws IOException {
        return client.index(joinIndexName(suffix), rawBlogList.toArray());
    }

    public void storeBulk(String suffix, List<RawBlog> rawBlogList) {
        rawBlogList.forEach(rawBlog ->
                bulkProcessor.add(new IndexRequest(joinIndexName(suffix), "_doc", GsonUtil.toJson(rawBlog))));
    }

    public List<RawBlog> query(String field, Object value, int pageSize, int page) throws IOException {
        QueryBuilder queryBuilder = QueryBuilders.termQuery(field, value);
        SearchResponse response = client.search(alias, queryBuilder, pageSize, page);
        return Optional.ofNullable(response)
                .map(SearchResponse::getHits)
                .map(SearchHits::getHits)
                .map(hits -> Stream.of(hits)
                        .map(hit -> GsonUtil.fromJson(hit.getSourceAsString(), RawBlog.class))
                        .collect(Collectors.toList()))
                .orElse(Lists.newArrayList());
    }


}
