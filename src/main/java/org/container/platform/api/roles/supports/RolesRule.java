package org.container.platform.api.roles.supports;

import java.util.List;

import lombok.Data;

import org.container.platform.api.common.CommonUtils;
import org.container.platform.api.common.Constants;

/**
 * Roles Rule model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.10.13
 */
@Data
public class RolesRule {
    private List apiGroups;
    private List nonResourceURLs;
    private List resourceNames;
    private List resources;
    private List verbs;

    public List getNonResourceURLs() {
        return CommonUtils.procReplaceNullValue(nonResourceURLs, Constants.ListObjectType.STRING);
    }

    public List getResourceNames() {
        return CommonUtils.procReplaceNullValue(resourceNames, Constants.ListObjectType.STRING);
    }
}
