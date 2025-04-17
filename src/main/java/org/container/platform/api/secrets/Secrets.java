package org.container.platform.api.secrets;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.container.platform.api.common.CommonUtils;
import org.container.platform.api.common.model.CommonAnnotations;
import org.container.platform.api.common.model.CommonMetaData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Secrets 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.07.31
 **/
@Data
public class Secrets {

    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    @JsonIgnore
    private CommonMetaData metadata;

    private String name;
    private String uid;
    private String namespace;
    private Object labels;
    private List<CommonAnnotations> annotations;
    private String creationTimestamp;
    private boolean immutable;
    private Map<String, String> data = new HashMap<>();
    private Map<String, Byte[]> binaryData = new HashMap<>();
    private String type;



    public String getName() {
        return name = metadata.getName();
    }

    public String getUid() {
        return uid = metadata.getUid();
    }

    public String getNamespace() {
        return namespace = metadata.getNamespace();
    }

    public Object getLabels() {
        return CommonUtils.procReplaceNullValue(metadata.getLabels());
    }

    public String getCreationTimestamp() {
        return creationTimestamp = metadata.getCreationTimestamp();
    }

}
