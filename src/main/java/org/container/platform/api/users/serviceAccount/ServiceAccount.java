package org.container.platform.api.users.serviceAccount;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.container.platform.api.common.model.CommonMetaData;
import java.util.List;
import java.util.Map;

/**
 * Service Account Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.12.07
 **/
@Data
public class ServiceAccount {
    private List<Map<String, String>> secrets;
    private String name;
    private String uid;
    private String namespace;

    @JsonIgnore
    private CommonMetaData metadata;

    public String getName() {
        return name = metadata.getName();
    }

    public String getUid() {
        return uid = metadata.getUid();
    }

    public String getNamespace() {
        return namespace = metadata.getNamespace();
    }
}
