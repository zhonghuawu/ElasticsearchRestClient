package com.huaa.rest.data;

import com.google.common.base.Joiner;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentGenerator;

import java.io.IOException;
import java.io.OutputStream;

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
            builder.field("index_patterns", Joiner.on("-").join(alias, "*"));
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
                builder.startObject("_default_");
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
                            builder.field("type", "keyword");
                        }
                        builder.endObject();
                        builder.startObject("timestamp");
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
        XContentGenerator generator = builder.generator();
        OutputStream os = builder.getOutputStream();
        byte[] bytes = new byte[10000];
        generator.writeUTF8String(bytes, 0, bytes.length);
        System.out.println(new String(bytes));
    }

}
