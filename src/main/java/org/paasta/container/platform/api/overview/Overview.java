package org.paasta.container.platform.api.overview;

import java.util.List;

import lombok.Data;

import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.overview.support.Status;

/**
 * Overview Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.24
 **/
@Data
public class Overview {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private String nextActionUrl;

    private Integer namespacesCount;
    private Integer deploymentsCount;
    private Integer podsCount;
    private Integer replicaSetsCount;
    private Integer usersCount;

    private List<Status> deploymentsUsage;
    private List<Status> podsUsage;
    private List<Status> replicaSetsUsage;

    public String getNextActionUrl() {
        return CommonUtils.procReplaceNullValue(nextActionUrl);
    }

    public Integer getNamespacesCount() {
        return  (namespacesCount < 1 ? 1 : namespacesCount);
    }
}