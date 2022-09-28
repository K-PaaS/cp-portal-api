package org.paasta.container.platform.api.clusters.cloudAccounts;

import lombok.Data;
import org.paasta.container.platform.api.common.Constants;

import java.util.Map;

@Data
public class ProviderInfoList {
    private String resultCode = Constants.RESULT_STATUS_FAIL;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private Map<String,Object> items;
}
