package org.paasta.container.platform.api.users;

import lombok.Data;
import org.paasta.container.platform.api.common.model.CommonItemMetaData;

import java.util.List;

/**
 * User List Admin Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.10.15
 **/
@Data
public class UsersListAdmin {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private CommonItemMetaData itemMetaData;
    private List<UserDetail> items;

    @Data
    public static class UserDetail {
        private String userId;
        private String serviceAccountName;
        private String cpNamespace;
        private String roleSetCode;
        private String userType; // do not delete
        private String created;

    }

}

