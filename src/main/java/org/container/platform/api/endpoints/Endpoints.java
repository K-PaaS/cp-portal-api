package org.container.platform.api.endpoints;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.container.platform.api.endpoints.support.EndPointsDetailsItem;
import org.container.platform.api.endpoints.support.EndpointSubset;

import java.util.List;

/**
 * Endpoints Admin Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.24
 */
@Data
public class Endpoints {

    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private List<EndPointsDetailsItem> endpoints;

    @JsonIgnore
    private List<EndpointSubset> subsets;


}
