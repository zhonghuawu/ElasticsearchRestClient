package com.huaa.rest;

import com.huaa.rest.clients.BlogRestClient;
import com.huaa.rest.core.ESRestClient;
import com.huaa.rest.data.Blog;
import com.huaa.util.DateUtil;

import java.io.IOException;

/**
 * Desc:
 *
 * @author Huaa
 * @date 2018/12/8 23:43
 */

public class RestHighLevelClientTest {

    private static String esips = "192.168.1.6:9200";

    private static ESRestClient esRestClient;
    private static BlogRestClient blogRestClient;
    static {
        esRestClient = new ESRestClient(esips);
        blogRestClient = new BlogRestClient(esRestClient);
    }

    public static void main(String[] args) throws IOException {
        System.out.println(blogRestClient.toString());
        store();
        esRestClient.close();
    }

    private static void store() throws IOException {
        Blog blog = new Blog("title0", "text0");
        System.out.println(blog);
        boolean created = blogRestClient.store(DateUtil.formatIndex(blog.getTimestamp()), blog);
        System.out.println("created: " + created);
    }

}
