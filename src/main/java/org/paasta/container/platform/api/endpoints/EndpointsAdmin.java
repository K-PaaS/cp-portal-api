package org.paasta.container.platform.api.endpoints;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.paasta.container.platform.api.endpoints.support.EndPointsDetailsItemAdmin;
import org.paasta.container.platform.api.endpoints.support.EndpointSubset;

import java.util.List;

/**
 * Endpoints Admin Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.17
 */
@Data
public class EndpointsAdmin {

    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private List<EndPointsDetailsItemAdmin> endpoints;

    @JsonIgnore
    private List<EndpointSubset> subsets;


}
