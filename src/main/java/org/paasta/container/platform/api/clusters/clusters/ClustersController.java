package org.paasta.container.platform.api.clusters.clusters;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Clusters Controller 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.06.30
 */
@RestController
@RequestMapping("/clusters")
public class ClustersController {
    private final ClustersService clustersService;


    /**
     * Instantiates a new Clusters controller
     *
     * @param clustersService the clusters service
     */
    @Autowired
    public ClustersController(ClustersService clustersService){
        this.clustersService = clustersService;
    }


    /**
     * Clusters 목록 조회(Get Clusters list)
     *
     * @param params the params
     * @return the Clusters list
     */
    @ApiOperation(value = "Clusters 목록 조회(Get Clusters list)", nickname = "getClustersList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping
    public ClustersList getClustersList(Params params){
        return clustersService.getClustersList(params);
    }


    /**
     * Clusters 상세 조회(Get Clusters detail)
     *
     * @param params the params
     * @return the Clusters detail
     */
    @ApiOperation(value = "Clusters 조회(Get Clusters Detail)", nickname = "getClusters")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/{cluster:.+}")
    public Clusters getClusters(Params params) {
        return clustersService.getClusters(params);
    }


    /**
     * Clusters 생성(Create Clusters)
     *
     * @param params the params
     * @return the Object
     */
    @ApiOperation(value = "Clusters 생성(Create Clusters)", nickname = "createClusters")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @PostMapping
    public Object createClusters(@RequestBody Params params){
        return clustersService.createClusters(params);
    }


    /**
     * Clusters 수정(Update Clusters)
     *
     * @param params the params
     * @return the Object
     */
    @ApiOperation(value = "Clusters 수정(Update Clusters)", nickname = "updateClusters")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @PutMapping
    public Object updateClusters(@RequestBody Params params){
        return clustersService.updateClusters(params);
    }


    /**
     * Clusters 삭제(Delete Clusters)
     *
     * @param params the params
     * @return the Object
     */
    @ApiOperation(value = "Clusters 삭제(Delete Clusters)", nickname = "deleteClusters")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @DeleteMapping(value = "/{cluster:.+}")
    public Object deleteClusters(Params params){
        return clustersService.deleteClusters(params);
    }
}
