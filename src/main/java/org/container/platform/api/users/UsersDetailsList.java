package org.container.platform.api.users;

import lombok.Data;
import org.container.platform.api.common.model.CommonItemMetaData;
import java.util.List;

@Data
public class UsersDetailsList {

    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private CommonItemMetaData itemMetaData;
    private List<UsersDetails> items;
}
