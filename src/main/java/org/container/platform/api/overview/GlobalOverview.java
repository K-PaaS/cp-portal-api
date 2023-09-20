package org.container.platform.api.overview;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.container.platform.api.metrics.TopNodes;

import java.util.List;

/**
 * Global Overview Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.06.29
 **/
@Data
@NoArgsConstructor
public class GlobalOverview {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    // count
    private Integer clusterStatus;
    private Integer namespaceStatus;
    private Integer pvcStatus;
    private Integer pvStatus;
    private Integer podStatus;

    private List<GlobalOverviewItems> items;

    private List<TopNodes> topNodeCPU;
    private List<TopNodes> topNodeMem;


    public GlobalOverview(Integer clusterStatus, Integer namespaceStatus, Integer pvcStatus, Integer pvStatus, Integer podStatus,
                          List<GlobalOverviewItems> items, List<TopNodes> topNodeCPU, List<TopNodes> topNodeMEM) {
        this.clusterStatus = clusterStatus;
        this.namespaceStatus = namespaceStatus;
        this.pvcStatus = pvcStatus;
        this.pvStatus = pvStatus;
        this.podStatus = podStatus;
        this.items = items;
        this.topNodeCPU = topNodeCPU;
        this.topNodeMem = topNodeMEM;
    }
}