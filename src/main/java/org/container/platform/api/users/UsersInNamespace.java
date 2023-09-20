package org.container.platform.api.users;

import lombok.Data;

import java.util.List;

/**
 * Users In Namespace Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.11.06
 **/
@Data
public class UsersInNamespace {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private String namespace;
    private List<UsersInfo> items;
}

@Data
class UsersInfo {
    public String userId;
    public String isNsAdmin;
}