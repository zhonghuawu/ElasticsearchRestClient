package com.huaa.rest;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.huaa.rest.clients.BlogRestClient;
import com.huaa.rest.core.ESRestClient;
import com.huaa.rest.data.RawBlog;
import com.huaa.util.DateUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Desc:
 *
 * @author Huaa
 * @date 2018/12/8 23:43
 */

public class RestHighLevelClientTest {

    private static String alpha_es_ips = "192.168.1.4:9200";

//    private static final String alpha_es_ips = "10.27.21.239:9200";

    private static ESRestClient esRestClient;
    private static BlogRestClient blogRestClient;

    static {
        esRestClient = new ESRestClient(alpha_es_ips);
        blogRestClient = new BlogRestClient(esRestClient);
    }

    public static void main(String[] args) throws IOException {
//        store(System.currentTimeMillis());
        query();
//        storeThreeMonth();
//
        esRestClient.close();
    }

    private static void storeThreeMonth() throws IOException {
        final long now = System.currentTimeMillis();
        final long oneDay = 1000L * 60 * 60 * 24;
        final int days = 30 * 3;
        IntStream.range(0, days)
                .forEach(day -> store(now - day * oneDay));
    }

    private static void store(long ts) {
        Random random = new Random();
        int n = 1;
        List<RawBlog> blogList = Lists.newArrayList();
        for (int i = 0; i < n; i++) {
            RawBlog blog = new RawBlog("title" + random.nextInt(100), "text" + random.nextLong());
            Map<String, String> tags = Maps.newHashMap();
            IntStream.range(0, random.nextInt(3))
                    .forEach(k -> tags.put("key" + random.nextInt(100), "value" + random.nextInt(100)));
            blog.setTags(tags);
            blogList.add(blog);
        }

        try {
            boolean created = blogRestClient.store(DateUtil.formatIndex(ts), blogList);
            System.out.println("created: " + created);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void query() throws IOException {
        List<RawBlog> rawBlogList = blogRestClient.query("title", "title0", 10, 1);
        rawBlogList.forEach(System.out::println);
    }

}
