package org.paasta.container.platform.api.users;

import lombok.Data;
import org.paasta.container.platform.api.common.Constants;

import java.util.List;

/**
 * User List Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.22
 **/
@Data
public class UsersList {
    public String resultCode;
    public String resultMessage;
    public Integer httpStatusCode;
    public String detailMessage;

    public String clusterType = Constants.EMPTY_STRING;
    private List<Users> items;

    public UsersList(){};
    public UsersList(List<Users> items) {
        this.items = items;
    }

}
