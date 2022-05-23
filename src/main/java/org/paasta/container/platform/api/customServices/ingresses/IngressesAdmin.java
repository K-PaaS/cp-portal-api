package org.paasta.container.platform.api.customServices.ingresses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.paasta.container.platform.api.common.model.CommonAnnotations;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.common.model.CommonSpec;
import org.paasta.container.platform.api.common.model.CommonStatus;

import java.util.List;

/**
 * Ingresses List Admin Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.17
 */
@Data
public class IngressesAdmin {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    //Details
    private String name;
    private String uid;
    private String namespace;
    private Object labels;
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
        return name;
    }

    public String getUid() {
        return uid;
    }

    public String getNamespace() {
        return namespace;
    }

    public Object getLabels() {
        return labels;
    }

    public String getCreationTimestamp() {
        return creationTimestamp;
    }
}
