package org.paasta.container.platform.api.endpoints;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Endpoints Controller 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.24
 */
@Api(value = "EndpointsController v1")
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
    @ApiOperation(value = "Endpoints 상세 조회(Get Endpoints detail)", nickname = "getEndpoints")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/{resourceName:.+}")
    public Object getEndpoints(Params params) {
        return endpointsService.getEndpoints(params);
    }
}
