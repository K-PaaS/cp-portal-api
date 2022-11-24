package org.paasta.container.platform.api.clusters.cloudAccounts;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.api.common.model.Params;
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
@Api(value = "CloudAccountsController v1")
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
    @ApiOperation(value = "CloudAccounts 목록 조회(Get CloudAccounts list)", nickname = "getCloudAccountsList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "CloudAccounts 타입 별 목록 조회(Get CloudAccounts list By Provider)", nickname = "getCloudAccountsListByProvider")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "CloudAccounts 상세 조회(Get CloudAccounts detail)", nickname = "getCloudAccounts")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "CloudAccounts 생성(Create CloudAccounts)", nickname = "createCloudAccounts")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "CloudAccounts 수정(Update CloudAccounts)", nickname = "updateCloudAccounts")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "CloudAccounts 삭제(Delete CloudAccounts)", nickname = "deleteCloudAccounts")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
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
    @ApiOperation(value = "ProviderInfo 조회(Get ProviderInfo)", nickname = "getProviderInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })

    @GetMapping(value = "/provider/info")
    public Object getProviderInfo(Params params) {
        return cloudAccountsService.getProviderInfoList(params);
    }


    @GetMapping(value = "/provider/info/{providerType:.+}")
    public Object getProviderInfoDetail(Params params) {
        return cloudAccountsService.getProviderInfo(params);
    }
}
