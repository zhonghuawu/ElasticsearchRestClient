package com.huaa.rest.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Desc:
 *
 * @author Huaa
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawTag implements Serializable {
    private String key;
    private String value;

}
