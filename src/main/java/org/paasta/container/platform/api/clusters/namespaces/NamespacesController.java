package org.paasta.container.platform.api.clusters.namespaces;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.ResultStatusService;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;

/**
 * Namespaces Controller 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2020.10.14
 */
@Api(value = "NamespacesController v1")
@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces")
public class NamespacesController {

    private final NamespacesService namespacesService;
    private final ResultStatusService resultStatusService;

    /**
     * Instantiates a new Namespaces controller
     *
     * @param namespacesService the namespaces service
     */
    @Autowired
    public NamespacesController(NamespacesService namespacesService, ResultStatusService resultStatusService
    ) {
        this.namespacesService = namespacesService;
        this.resultStatusService = resultStatusService;
    }


    /**
     * Namespaces 목록 조회(Get Namespaces list)
     *
     * @param cluster    the cluster
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @param isAdmin    the isAdmin
     * @return the namespaces list
     */
    @ApiOperation(value = "Namespaces 목록 조회(Get Namespaces list)", nickname = "getNamespacesList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "offset", value = "목록 시작지점, 기본값 0", required = false, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "한 페이지에 가져올 리소스 최대 수", required = false, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "orderBy", value = "정렬 기준, 기본값 creationTime(생성날짜)", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "order", value = "정렬 순서, 기본값 desc(내림차순)", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "searchName", value = "리소스 명 검색", required = false, dataType = "string", paramType = "query")
    })
    @GetMapping
    public Object getNamespacesList(@PathVariable(value = "cluster") String cluster,
                                    @RequestParam(required = false, defaultValue = "0") int offset,
                                    @RequestParam(required = false, defaultValue = "0") int limit,
                                    @RequestParam(required = false, defaultValue = "creationTime") String orderBy,
                                    @RequestParam(required = false, defaultValue = "") String order,
                                    @RequestParam(required = false, defaultValue = "") String searchName,
                                    @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {
        if (isAdmin) {
            return namespacesService.getNamespacesListAdmin(offset, limit, orderBy, order, searchName);
        }

        return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
    }

    /**
     * Namespaces 상세 조회(Get Namespaces detail)
     *
     * @param cluster   the cluster
     * @param namespace the namespace name
     * @param isAdmin   the isAdmin
     * @return the namespaces detail
     */
    @ApiOperation(value = "Namespaces 상세 조회(Get Namespaces detail)", nickname = "getNamespaces")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping("/{namespace:.+}")
    public Object getNamespaces(@PathVariable(value = "cluster") String cluster,
                                @PathVariable(value = "namespace") String namespace,
                                @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {
        if (isAdmin) {
            return namespacesService.getNamespacesAdmin(namespace);
        }

        return namespacesService.getNamespaces(namespace);
    }

    /**
     * Namespaces YAML 조회(Get Namespaces yaml)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @param isAdmin   the isAdmin
     * @return the namespaces yaml
     */
    @ApiOperation(value = "Namespaces YAML 조회(Get Nodes yaml)", nickname = "getNamespacesYaml")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
    })
    @GetMapping(value = "/{namespace:.+}/yaml")
    public Object getNamespacesYaml(@PathVariable(value = "cluster") String cluster,
                                    @PathVariable(value = "namespace") String namespace,
                                    @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        if (isAdmin) {
            return namespacesService.getNamespacesAdminYaml(namespace, new HashMap<>());
        }

        return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
    }

    /**
     * Namespaces 삭제(Delete Namespaces)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @param isAdmin   the isAdmin
     * @return return is succeeded
     */
    @ApiOperation(value = "Namespaces 삭제(Delete Namespaces)", nickname = "deleteNamespaces")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path")
    })
    @DeleteMapping(value = "/{namespace:.+}")
    public ResultStatus deleteNamespaces(@PathVariable(value = "cluster") String cluster,
                                         @PathVariable("namespace") String namespace,
                                         @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        if (isAdmin) {
            return namespacesService.deleteNamespaces(cluster, namespace);
        }
        return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
    }


    /**
     * Namespaces 생성(Create Namespaces)
     *
     * @param cluster      the cluster
     * @param initTemplate the init template
     * @param isAdmin      the isAdmin
     * @return return is succeeded
     */
    @ApiOperation(value = "Namespaces 생성(Create Namespaces)", nickname = "initNamespaces")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "initTemplate", value = "Namespace 생성 정보", required = true, dataType = "NamespacesInitTemplate", paramType = "body")
    })
    @PostMapping
    public ResultStatus initNamespaces(@PathVariable(value = "cluster") String cluster,
                                       @RequestBody NamespacesInitTemplate initTemplate,
                                       @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        if (initTemplate.getName().equals(Constants.NULL_REPLACE_TEXT) || initTemplate.getNsAdminUserId().equals(Constants.NULL_REPLACE_TEXT)) {
            return resultStatusService.REQUEST_VALUE_IS_MISSING();
        }

        if (initTemplate.getName().toLowerCase().equals(Constants.ALL_NAMESPACES))
            return resultStatusService.UNABLE_TO_CREATE_RESOURCE_NAME();

        if (isAdmin) {
            return namespacesService.createInitNamespaces(cluster, initTemplate);
        }

        return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
    }


    /**
     * Namespaces 수정(Update Namespaces)
     *
     * @param cluster      the cluster
     * @param namespace    the namespace
     * @param isAdmin      the isAdmin
     * @param initTemplate the init template
     * @return return is succeeded
     */
    @ApiOperation(value = "Namespaces 수정(modify Namespaces)", nickname = "modifyInitNamespaces")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "initTemplate", value = "Namespace 수정 정보", required = true, dataType = "NamespacesInitTemplate", paramType = "body")
    })
    @PutMapping(value = "/{namespace:.+}")
    public ResultStatus modifyInitNamespaces(@PathVariable(value = "cluster") String cluster,
                                             @PathVariable(value = "namespace") String namespace,
                                             @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin,
                                             @RequestBody NamespacesInitTemplate initTemplate) {

        if (initTemplate.getName().equals(Constants.NULL_REPLACE_TEXT) || initTemplate.getNsAdminUserId().equals(Constants.NULL_REPLACE_TEXT)) {
            return resultStatusService.REQUEST_VALUE_IS_MISSING();
        }

        if (isAdmin) {
            return namespacesService.modifyInitNamespaces(cluster, namespace, initTemplate);
        }

        return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
    }


    /**
     * Namespaces SelectBox 를 위한 Namespaces 목록 조회(Get Namespaces list for SelectBox)
     *
     * @param cluster the cluster
     * @param isAdmin the isAdmin
     * @return the namespaces list
     */
    @ApiOperation(value = "Namespaces selectbox를 위한 Namespace 목록 조회(Get Namespaces list for SelectBox)", nickname = "getNamespacesListForSelectBox")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping("/selectbox")
    public Object getNamespacesListForSelectBox(@PathVariable(value = "cluster") String cluster,
                                                @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {
        if (isAdmin) {
            return namespacesService.getNamespacesListForSelectbox();
        }

        return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
    }
}