package org.paasta.container.platform.api.users;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.api.accessInfo.AccessTokenService;
import org.paasta.container.platform.api.clusters.namespaces.NamespacesService;
import org.paasta.container.platform.api.common.CommonService;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.ResultStatusService;
import org.paasta.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;


/**
 * User Controller 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.25
 **/
@Api(value = "UsersController v1")
@RestController
public class UsersController {
    @Value("${cpNamespace.defaultNamespace}")
    private String defaultNamespace;

    private final UsersService usersService;
    private final ResultStatusService resultStatusService;
    private final NamespacesService namespacesService;
    private final CommonService commonService;
    private final AccessTokenService accessTokenService;

    @Autowired
    public UsersController(UsersService usersService, ResultStatusService resultStatusService, NamespacesService namespacesService, CommonService commonService, AccessTokenService accessTokenService) {
        this.usersService = usersService;
        this.resultStatusService = resultStatusService;
        this.namespacesService = namespacesService;
        this.commonService = commonService;
        this.accessTokenService = accessTokenService;
    }


    /**
     * Users 전체 목록 조회(Get Users List) -
     * 개발 0809 클러스터 관리자 목록 조회 (완)
     * 개발 0809 사용자 목록 조회
     * 개발 0819 클러스터 관리자 목록 조회 테스트
     * @param params the params
     * @return the users list
     */
    @ApiOperation(value = "Users 전체 목록 조회(Get Users list)", nickname = "getUsersList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/clusters/{cluster:.+}/namespaces/{namespace:.+}/usersList")
    public Object getUsersList(Params params) {
        if (params.getType().equalsIgnoreCase(Constants.SELECTED_ADMINISTRATOR)) {
            return usersService.getClusterAdminListByCluster(params);
        }
        return usersService.getUsersAllByCluster(params);

        // return usersService.getUsersAll(cluster,namespace,userId);
    }


    /**
     * 특정 Cluster 내 여러 Namespace 에 속한 User 에 대한 상세 조회(Get Users cluster namespace)
     * 개발 0811 사용자 상세 조회
     * @param params the params
     * @return the users detail
     */
    @ApiOperation(value = "하나의 Cluster 내 여러 Namespace 에 속한 User 에 대한 상세 조회(Get Users cluster namespace)", nickname = "getUsers")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/clusters/{cluster:.+}/users/{userAuthId:.+}")
    public UsersDetails getUsers(Params params) throws Exception {
        return usersService.getUsersDetailsByCluster(params);
    }



    /* //////////////////////////////////////////////////////////////////////////////////add info //////////////////////////////////////////////////////////////////////*/
    /**
     * 하나의 Cluster 내 여러 Namespace 에 속한 User 에 대한 상세 조회(Get Users Access Info)
     *
     * @param params the params
     * @return the users detail
     */
    @ApiOperation(value = "하나의 Cluster 내 여러 Namespace 에 속한 User 에 대한 상세 조회(Get Users Access Info)", nickname = "getUsersAccessInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/clusters/{cluster:.+}/namespaces/{namespace:.+}/accessesInfo")
    public Object getUsersAccessInfo(Params params) throws Exception {
        return usersService.getUsersAccessInfo(params);
    }


    /* //////////////////////////////////////////////////////////////////////////////////add info //////////////////////////////////////////////////////////////////////*/

    /**
     * Users 수정(Update Users)
     *
     * @param params the params
     * @return return is succeeded
     */
    @ApiOperation(value = "Users 수정(Update Users)", nickname = "modifyUsers")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @PutMapping(value = "/clusters/{cluster:.+}/users/{userId:.+}")
    public Object modifyUsers(Params params, @RequestBody Users users) throws Exception {
        if(users.getUserType().equalsIgnoreCase(Constants.AUTH_CLUSTER_ADMIN)) {
            return usersService.modifyToClusterAdmin(params, users);
        }
        return usersService.modifyToUser(params, users);
    }



    /**
     * Users 와 맵핑된 Clusters 목록 조회(Get Clusters List Used By User)
     *
     * @return the users list
     */
    @ApiOperation(value = "Users 와 맵핑된 Clusters 목록 조회(Get Clusters List Used By User)", nickname = "getClustersListByUserOwns")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/users/clustersList")
    public UsersList getClustersListByUserOwns(Params params) {
        return usersService.getMappingClustersListByUser(params);
    }


    /**
     * Users 와 맵핑된 Namespaces 목록 조회(Get Namespaces List Used By User)
     *
     * @return the users list
     */
    @ApiOperation(value = "Users 와 맵핑된 Namespaces 목록 조회(Get Namespaces List Used By User)", nickname = "getNamespacesListByUserOwns")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/clusters/{cluster:.+}/users/namespacesList")
    public UsersList getNamespacesListByUserOwns(Params params) {
        String userAuthority = commonService.getClusterAuthorityFromContext(params.getCluster());
        if (Constants.AUTH_ADMIN_LIST.contains(userAuthority)) {
            return namespacesService.getMappingNamespacesListByAdmin(params);
        }
        return usersService.getMappingNamespacesListByUser(params);
    }




}