package com.huaa.rest.data;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;

/**
 * Desc:
 *
 * @author Huaa
 * @date 2018/12/9 0:57
 */

public class TemplateUtil {

    private TemplateUtil() {}

    public static XContentBuilder blogTemplate(String alias) throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.field("order", 1000);
            builder.field("index_patterns", alias+"*");
            builder.startObject("settings");
            {
                builder.startObject("index");
                {
                    builder.field("number_of_shards", "3");
                    builder.field("number_of_replicas", "1");
                    builder.field("refresh_interval", "3s");
                    builder.field("max_result_window", 10000);
                }
                builder.endObject();
            }
            builder.endObject();
            builder.startObject("mappings");
            {
                builder.startObject("_doc");
                {
                    builder.startObject("properties");
                    {
                        builder.startObject("title");
                        {
                            builder.field("type", "keyword");
                        }
                        builder.endObject();
                        builder.startObject("text");
                        {
                            builder.field("type", "keyword");
                        }
                        builder.endObject();
                        builder.startObject("views");
                        {
                            builder.field("type", "long");
                        }
                        builder.endObject();
                        builder.startObject("tags");
                        {
                            builder.field("type", "nested");
                            builder.startObject("properties");
                            {
                                builder.startObject("key");
                                {
                                    builder.field("type", "keyword");
                                }
                                builder.endObject();
                                builder.startObject("value");
                                {
                                    builder.field("type", "keyword");
                                }
                                builder.endObject();
                            }
                            builder.endObject();
                        }
                        builder.endObject();
                        builder.startObject("ts");
                        {
                            builder.field("type", "date");
                        }
                        builder.endObject();
                    }
                    builder.endObject();
                    builder.startObject("_all");
                    {
                        builder.field("enabled", false);
                    }
                    builder.endObject();
                }
                builder.endObject();
            }
            builder.endObject();
            builder.startObject("aliases");
            {
                builder.startObject(alias);
                builder.endObject();
            }
            builder.endObject();
        }
        builder.endObject();
        return builder;
    }

    public static void main(String[] args) throws IOException {
        XContentBuilder builder = blogTemplate("blog").prettyPrint();
        builder.close();
    }

}
