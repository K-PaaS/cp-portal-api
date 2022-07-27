package org.paasta.container.platform.api.overview;

import java.util.List;

import lombok.Data;

import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.metrics.TopPods;
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
    private Integer replicaSetsCount;
    private Integer podsCount;
    private Integer usersCount;

    private List<Status> deploymentsUsage;
    private List<Status> replicaSetsUsage;
    private List<Status> podsUsage;
    private List<TopPods> topPodsCPU;
    private List<TopPods> topPodsMEM;

    public String getNextActionUrl() {
        return CommonUtils.procReplaceNullValue(nextActionUrl);
    }

    public Integer getNamespacesCount() {
        return  (namespacesCount < 1 ? 1 : namespacesCount);
    }


    public Overview(Integer namespacesCount, Integer deploymentsCount, Integer replicaSetsCount, Integer podsCount, Integer usersCount,
                    List<Status> deploymentsUsage, List<Status> replicaSetsUsage, List<Status> podsUsage, List<TopPods> topPodsCPU, List<TopPods> topPodsMEM) {
        this.namespacesCount = namespacesCount;
        this.deploymentsCount = deploymentsCount;
        this.replicaSetsCount = replicaSetsCount;
        this.podsCount = podsCount;
        this.usersCount = usersCount;
        this.deploymentsUsage = deploymentsUsage;
        this.replicaSetsUsage = replicaSetsUsage;
        this.podsUsage = podsUsage;
        this.topPodsCPU = topPodsCPU;
        this.topPodsMEM = topPodsMEM;
    }
}