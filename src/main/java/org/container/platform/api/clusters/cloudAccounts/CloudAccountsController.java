package org.container.platform.api.clusters.cloudAccounts;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * CloudAccounts Controller 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.06.30
 **/
@Tag(name = "CloudAccountsController v1")
@RestController
@PreAuthorize("@webSecurity.checkisGlobalAdmin()")
@RequestMapping("/accounts")
public class CloudAccountsController {
    private final CloudAccountsService cloudAccountsService;


    /**
     * Instantiates a new CloudAccounts controller
     *
     * @param cloudAccountsService the CloudAccounts service
     */
    @Autowired
    CloudAccountsController(CloudAccountsService cloudAccountsService) {
        this.cloudAccountsService = cloudAccountsService;
    }


    /**
     * CloudAccounts 목록 조회(Get CloudAccounts list)
     *
     * @param params the params
     * @return the CloudAccounts list
     */
    @Operation(summary = "CloudAccounts 목록 조회(Get CloudAccounts list)", operationId = "getCloudAccountsList")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping
    public CloudAccountsList getCloudAccountsList(Params params) {
        return cloudAccountsService.getCloudAccountsList(params);
    }


    /**
     * CloudAccounts 타입 별 목록 조회(Get CloudAccounts list By Provider)
     *
     * @param params the params
     * @return the CloudAccounts list
     */
    @Operation(summary = "CloudAccounts 타입 별 목록 조회(Get CloudAccounts list By Provider)", operationId = "getCloudAccountsListByProvider")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping(value = "/provider/{providerType:.+}")
    public CloudAccountsList getCloudAccountsListByProvider(Params params) {
        return cloudAccountsService.getCloudAccountsListByProvider(params);
    }

    /**
     * CloudAccounts 상세 조회(Get CloudAccounts detail)
     *
     * @param params the params
     * @return the CloudAccounts detail
     */
    @Operation(summary = "CloudAccounts 상세 조회(Get CloudAccounts detail)", operationId = "getCloudAccounts")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping(value = "/{resourceUid:.+}")
    public Object getCloudAccounts(Params params) {
        return cloudAccountsService.getCloudAccounts(params);
    }


    /**
     * CloudAccounts 생성(Create CloudAccounts)
     *
     * @param params the params
     * @return the Object
     */
    @PreAuthorize("@webSecurity.checkisSuperAdmin()")
    @Operation(summary = "CloudAccounts 생성(Create CloudAccounts)", operationId = "createCloudAccounts")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @PostMapping
    public Object createCloudAccounts(@RequestBody Params params) {
        cloudAccountsService.validationCheckCloudAccounts(params);
        return cloudAccountsService.createCloudAccounts(params);
    }


    /**
     * CloudAccounts 수정(Update CloudAccounts)
     *
     * @param params the params
     * @return the Object
     */
    @Operation(summary = "CloudAccounts 수정(Update CloudAccounts)", operationId = "updateCloudAccounts")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @PreAuthorize("@webSecurity.checkisSuperAdmin()")
    @PatchMapping
    public Object updateCloudAccounts(@RequestBody Params params) {
        return cloudAccountsService.updateCloudAccounts(params);
    }


    /**
     * CloudAccounts 삭제(Delete CloudAccounts)
     *
     * @param params the params
     * @return the Object
     */
    @Operation(summary = "CloudAccounts 삭제(Delete CloudAccounts)", operationId = "deleteCloudAccounts")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @PreAuthorize("@webSecurity.checkisSuperAdmin()")
    @DeleteMapping(value = "/{resourceUid:.+}")
    public Object deleteCloudAccounts(Params params) {
        return cloudAccountsService.deleteCloudAccounts(params);
    }


    /**
     * ProviderInfo 목록 조회(Get ProviderInfo List)
     *
     * @param params the params
     * @return the Object
     */
    @Operation(summary = "ProviderInfo 조회(Get Provider Info)", operationId = "getProviderInfo")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping(value = "/provider/info")
    public Object getProviderInfo(Params params) {
        return cloudAccountsService.getProviderInfoList(params);
    }

    @Operation(summary = "ProviderInfo 상세 조회(Get Provider Info Details)", operationId = "getProviderInfoDetail")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping(value = "/provider/info/{providerType:.+}")
    public Object getProviderInfoDetail(Params params) {
        return cloudAccountsService.getProviderInfo(params);
    }
}
