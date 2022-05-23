package org.paasta.container.platform.api.endpoints;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Endpoints Controller 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.11.06
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
     * @param cluster      the cluster
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @param isAdmin      the isAdmin
     * @return the endpoints detail
     */
    @ApiOperation(value = "Endpoints 상세 조회(Get Endpoints detail)", nickname = "getEndpoints")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceName", value = "리소스 명", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "/{resourceName:.+}")
    public Object getEndpoints(@PathVariable(value = "cluster") String cluster,
                               @PathVariable(value = "namespace") String namespace,
                               @PathVariable(value = "resourceName") String resourceName,
                               @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        // For Admin
        if (isAdmin) {
            return endpointsService.getEndpointsAdmin(namespace, resourceName);
        }

        return endpointsService.getEndpoints(namespace, resourceName);
    }


}
