package org.container.platform.api.clusters.hclTemplates;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.container.platform.api.common.model.Params;
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
@Tag(name = "HclTemplatesController v1")
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
    @Operation(summary = "HclTemplates 상세 조회(Get HclTemplates)", operationId = "getHclTemplates")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "HclTemplates 목록 조회(Get HclTemplates List)", operationId = "getHclTemplatesList")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "HclTemplates 타입 별 목록 조회(Get HclTemplates List By Provider)", operationId = "getHclTemplatesListByProvider")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "HclTemplates 생성(Create HclTemplates)", operationId = "createHclTemplates")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "HclTemplates 수정(Update HclTemplates)", operationId = "updateHclTemplates")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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
    @Operation(summary = "HclTemplates 삭제(Delete HclTemplates)", operationId = "deleteHclTemplates")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @PreAuthorize("@webSecurity.checkisSuperAdmin()")
    @DeleteMapping(value = "/{resourceUid:.+}")
    public Object deleteHclTemplates(Params params) {
        return hclTemplatesService.deleteHclTemplates(params);
    }
}
