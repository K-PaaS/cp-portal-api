package org.paasta.container.platform.api.signUp;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.PropertyService;
import org.paasta.container.platform.api.common.ResultStatusService;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.config.NoAuth;
import org.paasta.container.platform.api.users.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Sign Up Controller 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.22
 **/
@Api(value = "SignUpController v1")
@RestController
public class SignUpController {

    private final SignUpUserService signUpUserService;
    private final SignUpAdminService signUpAdminService;
    private final PropertyService propertyService;
    private final ResultStatusService resultStatusService;

    /**
     * Instantiates a new SignUp controller
     *
     * @param signUpUserService the signUpUserService service
     * @param signUpAdminService the signUpAdminService service
     */
    @Autowired
    public SignUpController(SignUpUserService signUpUserService, SignUpAdminService signUpAdminService,PropertyService propertyService, ResultStatusService resultStatusService) {
        this.signUpUserService = signUpUserService;
        this.signUpAdminService = signUpAdminService;
        this.propertyService = propertyService;
        this.resultStatusService = resultStatusService;
    }


    /**
     * 회원가입(Sign Up)
     *
     * @param requestUsers the requestUsers
     * @param isAdmin the isAdmin
     * @return the resultStatus
     */
    @ApiOperation(value = "회원가입(Sign Up)", nickname = "signUpUsers")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "requestUsers", value = "요청한 유저", required = true, dataType = "Object", paramType = "body"),
            @ApiImplicitParam(name = "isAdmin", value = "관리자 여부 (true/false)", required = true, dataType = "string", paramType = "query")
    })
    @NoAuth
    @PostMapping(value = Constants.URI_SIGN_UP)
    public ResultStatus signUpUsers(@RequestBody Object requestUsers,
                                    @RequestParam(required = false, name = "isAdmin", defaultValue = "false") String isAdmin,
                                    @RequestParam(required = false, name = "param", defaultValue = "") String param) {

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> map = objectMapper.convertValue(requestUsers, Map.class);

        Users users = objectMapper.convertValue(map, Users.class);

        if(users.getUserId().equalsIgnoreCase(Constants.NULL_REPLACE_TEXT) || users.getUserAuthId().equalsIgnoreCase(Constants.NULL_REPLACE_TEXT)) {
            return resultStatusService.INVALID_USER_SIGN_UP();
        }

        // Converts a userId to lowercase letters
        users.setUserId(users.getUserId().toLowerCase());

        // For Cluster Admin
        if(isAdmin.toLowerCase().equals(Constants.CHECK_TRUE)) {
            return signUpAdminService.signUpAdminUsers(users, param);
        }

        // For User As Service Type
        if(users.getCpProviderType().equalsIgnoreCase(propertyService.getCpProviderAsService())) {
           return  signUpUserService.signUpUsersByProviderAsService(users);
        }

        // For User As StandAlone Type
          return signUpUserService.signUpUsersByProviderAsStandAlone(users);
    }


    /**
     * Users 이름 목록 조회(Get Users names list)
     *
     * @return the map
     */
    @ApiOperation(value = "Users 이름 목록 조회(Get Users names list)", nickname = "getUsersNameList")
    @GetMapping(value = "/users/names")
    public Map<String, List<String>> getUsersNameList() {
        return signUpUserService.getUsersNameList();
    }



}
