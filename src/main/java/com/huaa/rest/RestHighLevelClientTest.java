package com.huaa.rest;

import com.google.common.collect.Maps;
import com.huaa.rest.clients.BlogRestClient;
import com.huaa.rest.core.ESRestClient;
import com.huaa.rest.data.RawBlog;
import com.huaa.util.DateUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Desc:
 *
 * @author Huaa
 * @date 2018/12/8 23:43
 */

public class RestHighLevelClientTest {

    private static String esips = "192.168.1.4:9200";

    private static ESRestClient esRestClient;
    private static BlogRestClient blogRestClient;

    static {
        esRestClient = new ESRestClient(esips);
        blogRestClient = new BlogRestClient(esRestClient);
    }

    public static void main(String[] args) throws IOException {
        store();
//        query();

        esRestClient.close();
    }

    private static void store() throws IOException {
        Random random = new Random();
        int n = 1000;
        RawBlog[] blogs = new RawBlog[n];
        for (int i=0; i<n; i++) {
            blogs[i] = new RawBlog("title" + random.nextInt(100), "text" + random.nextLong());
            if (i%5==0) {
                int k = random.nextInt(10);
                Map<String, String> tags = Maps.newHashMap();
                while(k-- != 0) {
                    tags.put("key"+random.nextInt(100), "value"+random.nextInt(100));
                }
                blogs[i].setTags(tags);
            }
        }

        boolean created = blogRestClient.store(DateUtil.formatIndex(System.currentTimeMillis()), blogs);
        System.out.println("created: " + created);
        esRestClient.close();
    }

    private static void query() throws IOException {
        List<RawBlog> rawBlogList = blogRestClient.query("title", "title0", 10, 1);
        rawBlogList.forEach(System.out::println);
    }

}
