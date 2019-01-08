package com.huaa.rest.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Desc:
 *
 * @author Huaa
 * @date 2018/12/9 0:55
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawBlog implements Serializable {

    private static final long serialVersionUID = -8570814277602330021L;

    private String title;
    private String text;
    private List<RawTag> tags;
    private long views;
    private long ts;

    public RawBlog(String title, String text) {
        this.title = title;
        this.text = text;
        this.views = 0L;
        this.tags = Lists.newArrayList();
        this.ts = System.currentTimeMillis();
    }

    public void setTags(Map<String, String> tagMap) {
        if (tagMap == null) {
            return;
        }
        for (Map.Entry<String, String> entry : tagMap.entrySet()) {
            this.tags.add(new RawTag(entry.getKey(), entry.getValue()));
        }
    }

    public Map<String, String> getTags() {
        Map<String, String> result = Maps.newHashMap();
        if (this.tags == null) {
            return result;
        }
        for (RawTag rawTag : this.tags) {
            result.put(rawTag.getKey(), rawTag.getValue());
        }
        return result;
    }

}
