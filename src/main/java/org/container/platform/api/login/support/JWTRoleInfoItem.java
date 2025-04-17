package org.container.platform.api.login.support;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class JWTRoleInfoItem {
    private String userType;
    private List<String> namespaceList;

    public JWTRoleInfoItem(String userType) {
        this.userType = userType;
    }

    public JWTRoleInfoItem(String userType, String namespace) {
        this.userType = userType;
        this.namespaceList = new ArrayList<>();
        this.namespaceList.add(namespace);
    }


}
