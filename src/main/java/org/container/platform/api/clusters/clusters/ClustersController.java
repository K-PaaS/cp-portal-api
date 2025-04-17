package org.container.platform.api.clusters.clusters;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Clusters Controller 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.06.30
 */
@RestController
@Tag(name = "ClustersController v1")
@PreAuthorize("@webSecurity.checkisGlobalAdmin()")
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
    @Operation(summary = "Clusters 목록 조회(Get Clusters list)", operationId = "getClustersList")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "Clusters 조회(Get Clusters Detail)", operationId = "getClusters")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "Clusters 생성(Create Clusters)", operationId = "createClusters")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @PreAuthorize("@webSecurity.checkisSuperAdmin()")
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
    @Operation(summary = "Clusters 수정(Update Clusters)", operationId = "updateClusters")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @PreAuthorize("@webSecurity.checkisSuperAdmin()")
    @PatchMapping
    public Object updateClusters(@RequestBody Params params){
        return clustersService.updateClusters(params);
    }


    /**
     * Clusters 삭제(Delete Clusters)
     *
     * @param params the params
     * @return the Object
     */
    @Operation(summary = "Clusters 삭제(Delete Clusters)", operationId = "deleteClusters")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @PreAuthorize("@webSecurity.checkisSuperAdmin()")
    @DeleteMapping(value = "/{cluster:.+}")
    public Object deleteClusters(Params params){
        return clustersService.deleteClusters(params);
    }
}
