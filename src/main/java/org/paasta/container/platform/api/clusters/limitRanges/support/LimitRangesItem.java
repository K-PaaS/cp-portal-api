package org.paasta.container.platform.api.clusters.limitRanges.support;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

import lombok.Data;

import org.springframework.util.ObjectUtils;

import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.Constants;

/**
 * LimitRanges Item Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.24
 */
@Data
public class LimitRangesItem {
    private String type;
    private String resource;
    private Object min;
    private Object max;
    private Object defaultRequest;

    @SerializedName("default")
    private Object defaultLimit;

    public Object getDefaultLimit() {
        return (ObjectUtils.isEmpty(defaultLimit)) ? Constants.NULL_REPLACE_TEXT : defaultLimit;
    }

    public Object getDefaultRequest() {
        return (ObjectUtils.isEmpty(defaultRequest)) ? Constants.NULL_REPLACE_TEXT : defaultRequest;
    }

    public Object getMin() {
        return CommonUtils.procReplaceNullValue(min);
    }

    public Object getMax() {
        return CommonUtils.procReplaceNullValue(max);
    }

    public String getResource() {
        return CommonUtils.procReplaceNullValue(resource);
    }
}
