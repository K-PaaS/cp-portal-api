package org.paasta.container.platform.api.users;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.MessageConstant;
import org.paasta.container.platform.api.common.ResultStatusService;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

import static org.paasta.container.platform.api.common.CommonUtils.regexMatch;

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

    @Autowired
    public UsersController(UsersService usersService, ResultStatusService resultStatusService) {
        this.usersService = usersService;
        this.resultStatusService = resultStatusService;
    }



    /**
     * Users 전체 목록 조회(Get Users List)
     *
     * @param params the params
     * @return the users list
     */
    @ApiOperation(value = "Users 전체 목록 조회(Get Users list)", nickname = "getUsersList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/clusters/{cluster:.+}/users")
    public Object getUsersList(Params params) {
        if(params.getUserType().equalsIgnoreCase(Constants.SELECTED_ADMINISTRATOR)) {
            return usersService.getClusterAdminAllByCluster(params);
        }
        return usersService.getUsersAllByCluster(params);

        // return usersService.getUsersAll(cluster,namespace,userId);
    }


    /**
     * 하나의 Cluster 내 여러 Namespace 에 속한 User 에 대한 상세 조회(Get Users cluster namespace)
     *
     * @param params the params
     * @return the users detail
     */
    @ApiOperation(value = "하나의 Cluster 내 여러 Namespace 에 속한 User 에 대한 상세 조회(Get Users cluster namespace)", nickname = "getUsers")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/clusters/{cluster:.+}/users/{userId:.+}")
    public Object getUsers(Params params) throws Exception {
        if(params.getUserType().equalsIgnoreCase(Constants.SELECTED_ADMINISTRATOR)) {
            params.setUserType(Constants.AUTH_CLUSTER_ADMIN);
        }
        else {
            //사용자인 경우
            params.setUserType(Constants.AUTH_USER);
        }

        return usersService.getUsersInMultiNamespace(params);
    }


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
        return usersService.modifyUsersAdmin(params, users);
    }





    /**
     * Namespace Users 목록 조회(Get Users List By Namespaces)
     *
     * @param params the params
     * @return the users list
     */
    @ApiOperation(value = "Namespace Users 목록 조회(Get Users List By Namespaces)", nickname = "getUsersListInNamespace")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/clusters/{cluster:.+}/namespaces/{namespace:.+}/users/namespace")
    public Object getUsersListInNamespace(Params params) {
        return usersService.getUsersListInNamespaceAdmin(params);
        // return usersService.getUsersListByNamespace(cluster, namespace);
    }


    /**
     * 특정 Namespace 관리자 판별이 포함된 Users Name 목록 조회(Get Users Name List containing Namespace Admin)
     *
     * @param params the params
     * @return the UsersInNamespace
     */
    @ApiOperation(value = "특정 Namespace 관리자 판별이 포함된 Users Name 목록 조회(Get Users Name List containing Namespace Admin)", nickname = "getUsersNameListByNamespaceAdmin")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/clusters/{cluster:.+}/namespaces/{namespace:.+}/users/adminCheck")
    public UsersInNamespace getUsersNameListByNamespaceAdmin(Params params) {
        return usersService.getUsersNameListByNamespaceAdmin(params);
    }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//사용자 포탈 기능, 운영자 포탈에 적용될 수 있으므로 보류



    /**
     * 각 Namespace 별 Users 목록 조회(Get Users namespace list)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @param isAdmin   the isAdmin
     * @return the users list
     */
    @ApiOperation(value = "각 Namespace 별 Users 목록 조회(Get Users namespace list)", nickname = "getUsersListByNamespace")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "/clusters/{cluster:.+}/namespaces/{namespace:.+}/users")
    public Object getUsersListByNamespace(@PathVariable(value = "cluster") String cluster,
                                          @PathVariable(value = "namespace") String namespace,
                                          @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        if (isAdmin) {
            return usersService.getUsersListByNamespaceAdmin(cluster, namespace);
        }
        return usersService.getUsersListByNamespace(cluster, namespace);
    }




    /**
     * Users 상세 조회(Get Users detail)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @param userId    the userId
     * @return the users list
     */
    @ApiOperation(value = "각 Namespace 별 Users 상세 조회(Get Users namespace detail)", nickname = "getUsersByNamespace")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "userId", value = "유저 Id", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "/clusters/{cluster:.+}/namespaces/{namespace:.+}/users/{userId:.+}")
    public Object getUsersByNamespace(@PathVariable(value = "cluster") String cluster,
                                      @PathVariable(value = "namespace") String namespace,
                                      @PathVariable(value = "userId") String userId) {
        if (namespace.toLowerCase().equals(Constants.ALL_NAMESPACES)) {
            return usersService.getClusterAdminUsers(cluster, userId);
        }

        return usersService.getUsers(cluster, namespace, userId);
    }






    /**
     * 각 Namespace 별 등록 되어 있는 사용자들의 이름 목록 조회(Get Users registered list namespace)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @return the users list
     */
    @ApiOperation(value = "각 Namespace 별 등록 되어 있는 사용자들의 이름 목록 조회(Get Users registered list namespace)", nickname = "getUsersNameList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path")
    })
    @GetMapping(value = "/clusters/{cluster:.+}/namespaces/{namespace:.+}/users/names")
    public Map<String, List> getUsersNameList(@PathVariable(value = "cluster") String cluster,
                                              @PathVariable(value = "namespace") String namespace) {
        return usersService.getUsersNameListByNamespace(cluster, namespace);
    }





    /**
     * 운영자 정보 수정 (Update admin info)
     *
     * @param cluster   the cluster
     * @param namespace the namespace
     * @param userId    the user id
     * @param users     the users
     * @param isAdmin   the isAdmin
     * @return return is succeeded
     */
    @ApiOperation(value = "Users 수정(Update Users)", nickname = "modifyUsers")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "userId", value = "유저 Id", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "users", value = "유저", required = true, dataType = "Users", paramType = "body")
    })
    @PutMapping(value = "/clusters/{cluster:.+}/namespaces/{namespace:.+}/users/{userId:.+}")
    public Object modifyUsersInfo(@PathVariable(value = "cluster") String cluster,
                                  @PathVariable(value = "namespace") String namespace,
                                  @PathVariable(value = "userId") String userId,
                                  @RequestBody Users users,
                                  @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        // input parameter regex
        if (!Constants.RESULT_STATUS_SUCCESS.equals(regexMatch(users))) {
            return ResultStatus.builder().resultCode(Constants.RESULT_STATUS_FAIL)
                    .resultMessage(MessageConstant.RE_CONFIRM_INPUT_VALUE.getMsg())
                    .httpStatusCode(400)
                    .detailMessage(regexMatch(users)).build();
        }

        if (isAdmin) {
            return usersService.modifyUsers(userId, users);
        }

        return resultStatusService.FORBIDDEN_ACCESS_RESULT_STATUS();
    }


    /*
     */
/**
 * Users 권한 설정(Set Users authority)
 *
 * @param cluster   the cluster
 * @param namespace the namespace
 * @param users     the users
 * @return return is succeeded
 *//*

    @ApiOperation(value = "Users 권한 설정(Set Users authority)", nickname = "modifyUsersConfig")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "users", value = "유저 목록", required = true, dataType = "List<Users>", paramType = "body")
    })
    @PutMapping(value = "/clusters/{cluster:.+}/namespaces/{namespace:.+}/users")
    public ResultStatus modifyUsersConfig(@PathVariable(value = "cluster") String cluster,
                                          @PathVariable(value = "namespace") String namespace,
                                          @RequestBody List<Users> users) {
        return usersService.modifyUsersConfig(cluster, namespace, users);
    }

*/

}