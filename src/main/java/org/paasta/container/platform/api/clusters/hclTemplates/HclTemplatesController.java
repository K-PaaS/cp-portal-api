package org.paasta.container.platform.api.clusters.hclTemplates;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * HclTemplates Controller 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.06.30
 */
@RestController
@RequestMapping("/templates")
@PreAuthorize("@webSecurity.checkisGlobalAdmin()")
public class HclTemplatesController {
    private final HclTemplatesService hclTemplatesService;


    /**
     * Instantiates a new HclTemplates controller
     *
     * @param hclTemplatesService the hclTemplates service
     */
    @Autowired
    HclTemplatesController(HclTemplatesService hclTemplatesService) {
        this.hclTemplatesService = hclTemplatesService;
    }


    /**
     * HclTemplates 상세 조회(Get HclTemplates detail)
     *
     * @param params the params
     * @return the hclTemplates detail
     */
    @ApiOperation(value = "HclTemplates 상세 조회(Get HclTemplates)", nickname = "getHclTemplates")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/{resourceUid:.+}")
    public Object getHclTemplates(Params params) {
        return hclTemplatesService.getHclTemplates(params);
    }


    /**
     * HclTemplates 목록 조회(Get HclTemplates list)
     *
     * @param params the params
     * @return the hclTemplates list
     */
    @ApiOperation(value = "HclTemplates 목록 조회(Get HclTemplates List)", nickname = "getHclTemplatesList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping
    public HclTemplatesList getHclTemplatesList(Params params) {
        return hclTemplatesService.getHclTemplatesList(params);
    }

    /**
     * HclTemplates 타입 별 목록 조회(Get HclTemplates list By Provider)
     *
     * @param params the params
     * @return the hclTemplates list
     */
    @ApiOperation(value = "HclTemplates 타입 별 목록 조회(Get HclTemplates List By Provider)", nickname = "getHclTemplatesListByProvider")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/provider/{providerType:.+}")
    public HclTemplatesList getHclTemplatesListByProvider(Params params) {
        return hclTemplatesService.getHclTemplatesListByProvider(params);
    }


    /**
     * HclTemplates 생성(Create HclTemplates)
     *
     * @param params the params
     * @return Object
     */
    @ApiOperation(value = "HclTemplates 생성(Create HclTemplates)", nickname = "createHclTemplates")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @PreAuthorize("@webSecurity.checkisSuperAdmin()")
    @PostMapping
    public Object createHclTemplates(@RequestBody  Params params) {
        return hclTemplatesService.createHclTemplates(params);
    }


    /**
     * HclTemplates 수정(Update HclTemplates)
     *
     * @param params the params
     * @return Object
     */
    @ApiOperation(value = "HclTemplates 수정(Update HclTemplates)", nickname = "updateHclTemplates")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @PreAuthorize("@webSecurity.checkisSuperAdmin()")
    @PutMapping
    public Object updateHclTemplates(@RequestBody Params params) {
        return hclTemplatesService.updateHclTemplates(params);
    }


    /**
     * HclTemplates 삭제(Delete HclTemplates)
     *
     * @param params the params
     * @return the Object
     */
    @ApiOperation(value = "HclTemplates 삭제(Delete HclTemplates)", nickname = "deleteHclTemplates")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @PreAuthorize("@webSecurity.checkisSuperAdmin()")
    @DeleteMapping(value = "/{resourceUid:.+}")
    public Object deleteHclTemplates(Params params) {
        return hclTemplatesService.deleteHclTemplates(params);
    }
}
