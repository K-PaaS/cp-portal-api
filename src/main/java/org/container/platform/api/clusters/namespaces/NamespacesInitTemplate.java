package org.container.platform.api.clusters.namespaces;

import lombok.Data;
import org.container.platform.api.common.CommonUtils;

import java.util.List;

/**
 * Namespaces Init Template Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.24
 **/
@Data
public class NamespacesInitTemplate {
    private String name;
    private String nsAdminUserId;
    private List<String> resourceQuotasList;
    private List<String> limitRangesList;

    public String getName() {
        return  CommonUtils.procReplaceNullValue(name.trim());
    }

    public String getNsAdminUserId() {
        return CommonUtils.procReplaceNullValue(nsAdminUserId);
    }
}
