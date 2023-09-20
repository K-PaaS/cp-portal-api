package org.container.platform.api.clusters.resourceQuotas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.container.platform.api.common.model.CommonMetaData;
import org.container.platform.api.common.model.CommonSpec;

import javax.management.monitor.CounterMonitor;

/**
 * ResourceQuotas Default Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.10.26
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceQuotasDefault {
    @JsonIgnore
    private String id;

    private String name;
    private String requestCpu;
    private String requestMemory;
    private String limitCpu;
    private String limitMemory;

    private String status;
    private String checkYn;
    private String creationTimestamp;

    @JsonIgnore
    private CommonMetaData metadata;

    @JsonIgnore
    private CommonSpec spec;

    public ResourceQuotasDefault(String name, String status, String checkYn, CommonMetaData metadata, String creationTimestamp) {
        this.name = name;
        this.status = status;
        this.checkYn = checkYn;
        this.metadata = metadata;
        this.creationTimestamp = creationTimestamp;
    }
}
