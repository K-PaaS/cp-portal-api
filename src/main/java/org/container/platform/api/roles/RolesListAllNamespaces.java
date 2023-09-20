package org.container.platform.api.roles;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.Map;

import lombok.Data;

import org.container.platform.api.common.Constants;
import org.container.platform.api.common.model.CommonItemMetaData;
import org.container.platform.api.common.model.CommonMetaData;

/**
 * Roles List AllNamespaces Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.11.04
 **/
@Data
public class RolesListAllNamespaces {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private Map metadata;
    private CommonItemMetaData itemMetaData;
    private List<RolesListAllNamespacesItem> items;


    @Data
    public static class RolesListAllNamespacesItem {
        private String name;
        private String namespace;
        private String checkYn;
        private String userType;
        private String isNamespaceAdminRole = Constants.CHECK_N;

        @JsonIgnore
        private CommonMetaData metadata;

        public String getName() {
            return metadata.getName();
        }

        public String getNamespace() {
            return metadata.getNamespace();
        }

    }
}