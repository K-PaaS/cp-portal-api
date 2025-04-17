package org.container.platform.api.endpoints;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints Controller 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.24
 */
@Tag(name = "EndpointsController v1")
@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/endpoints")
public class EndpointsController {

    private final EndpointsService endpointsService;

    /**
     * Instantiates a new Endpoints controller
     *
     * @param endpointsService the endpoints service
     */
    @Autowired
    public EndpointsController(EndpointsService endpointsService) {
        this.endpointsService = endpointsService;
    }


    /**
     * Endpoints 상세 조회(Get Endpoints detail)
     *
     * @param params the params
     * @return the endpoints detail
     */
    @Operation(summary = "Endpoints 상세 조회(Get Endpoints detail)", operationId = "getEndpoints")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping(value = "/{resourceName:.+}")
    public Object getEndpoints(Params params) {
        return endpointsService.getEndpoints(params);
    }
}
