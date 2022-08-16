package org.paasta.container.platform.api.users;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Users Admin Model 클래스
 *
 * @author kjh
 * @version 1.0
 * @since 2021.06.25
 */


@Data
@NoArgsConstructor
public class UsersDetails {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;


    private String userId;
    private String userAuthId;
    private String userType;
    private String serviceAccountName;
    private String created;

    private List<Users> items;



    public UsersDetails(String userId, String userAuthId, String serviceAccountName, String created, List<Users> items) {
        this.userId = userId;
        this.userAuthId = userAuthId;
        this.serviceAccountName = serviceAccountName;
        this.created = created;
        this.items = items;
    }
}


