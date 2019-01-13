package com.huaa.rest.clients;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.huaa.rest.core.ESRestClient;
import com.huaa.rest.data.RawBlog;
import com.huaa.rest.data.TemplateUtil;
import com.huaa.util.GsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;

import java.io.IOException;
import java.util.List;

/**
 * Desc:
 *
 * @author Huaa
 * @date 2018/12/9 1:01
 */

public class BlogRestClient {
    private static Logger logger = LogManager.getLogger(BlogRestClient.class);

    private ESRestClient client;

    private static String alias = "blog";
    private static String templateName = alias + "-template";

    public BlogRestClient(ESRestClient client) {
        this.client = client;
        try {
            boolean result = putTemplate();
            if (! result)
            {
                logger.error("putTemplate failed, {}", alias);
            }
        } catch (IOException e) {
            logger.error("putTemplate failed, {}", alias, e);
        }
    }

    private boolean putTemplate() throws IOException {
        if (client.isExistedTemplate(templateName)) {
            return true;
        }
        XContentBuilder templateSource = TemplateUtil.blogTemplate(alias);
        return client.putTemplate(templateName, templateSource);
    }

    private static String joinIndexName(String suffix)
    {
        return Joiner.on("-").join(alias, suffix);
    }

    public boolean store(String suffix, RawBlog... rawBlogs) throws IOException {
        return client.index(joinIndexName(suffix), rawBlogs);
    }

    public List<RawBlog> query(String field, Object value, int pageSize, int page) throws IOException {
        QueryBuilder queryBuilder = QueryBuilders.termQuery(field, value);
        SearchResponse response = client.search(alias, queryBuilder, pageSize, page);
        SearchHits hits = response.getHits();
        List<RawBlog> results = Lists.newArrayList();
        hits.forEach(hit -> results.add(GsonUtil.fromJson(hit.getSourceAsString(), RawBlog.class)));
        return results;
    }


}
