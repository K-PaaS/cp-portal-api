package org.paasta.container.platform.api.customServices.services;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.model.CommonAnnotations;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.common.model.CommonSpec;
import org.paasta.container.platform.api.common.model.CommonStatus;

import java.util.List;

/**
 * CustomServices Admin Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.24
 */
@Data
public class CustomServices {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    //Details
    private String name;
    private String uid;
    private String namespace;
    private Object labels;
    private List port;
    private List<CommonAnnotations> annotations;
    private String creationTimestamp;

    //Resource Info
    private String type;
    private String clusterIP;
    private String sessionAffinity;
    private Object selector;

    @JsonIgnore
    private CommonMetaData metadata;

    @JsonIgnore
    private CommonSpec spec;

    @JsonIgnore
    private CommonStatus status;

    public String getName() {
        return metadata.getName();
    }

    public String getUid() {
        return metadata.getUid();
    }

    public String getNamespace() {
        return metadata.getNamespace();
    }

    public Object getLabels() {
        return CommonUtils.procReplaceNullValue(metadata.getLabels());
    }

    public String getCreationTimestamp() {
        return metadata.getCreationTimestamp();
    }

    public String getType() {
        return spec.getType();
    }

    public String getClusterIP() {
        return spec.getClusterIP();
    }

    public String getSessionAffinity() {
        return spec.getSessionAffinity();
    }

    public Object getSelector() {
        return spec.getSelector();
    }

    public List getPort() {
        return spec.getPorts();
    }
}
