package org.paasta.container.platform.api.signUp;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.MessageConstant;
import org.paasta.container.platform.api.common.PropertyService;
import org.paasta.container.platform.api.common.ResultStatusService;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.config.NoAuth;
import org.paasta.container.platform.api.exception.ResultStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Sign Up Controller 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.06.02
 **/
@Api(value = "SignUpController v1")
@RestController
public class SignUpController {

    private final SignUpUserService signUpUserService;
    private final SignUpService signUpService;
    private final PropertyService propertyService;
    private final ResultStatusService resultStatusService;

    /**
     * Instantiates a new SignUp controller
     *
     * @param signUpUserService the signUpUserService service
     * @param signUpService     the signUpService
     */
    @Autowired
    public SignUpController(SignUpUserService signUpUserService, SignUpService signUpService, PropertyService propertyService, ResultStatusService resultStatusService) {
        this.signUpUserService = signUpUserService;
        this.signUpService = signUpService;
        this.propertyService = propertyService;
        this.resultStatusService = resultStatusService;
    }


    /**
     * 회원가입(Sign Up)
     *
     * @param params the params
     * @return the resultStatus
     */
    @ApiOperation(value = "회원가입(Sign Up)", nickname = "signUpUsers")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @NoAuth
    @PostMapping(value = Constants.URI_SIGN_UP)
    public ResultStatus signUpUsers(@RequestBody Params params) {
        if (params.getUserId().equalsIgnoreCase(Constants.EMPTY_STRING) || params.getUserAuthId().equalsIgnoreCase(Constants.EMPTY_STRING)) {
            throw new ResultStatusException(MessageConstant.USER_SIGN_UP_INFO_REQUIRED.getMsg());
        }

        if (params.getIsSuperAdmin()) {
            params.setUserType(Constants.AUTH_SUPER_ADMIN);
        } else {
            params.setUserType(Constants.AUTH_USER);
        }

        // Converts a userId to lowercase letters
        params.setUserId(params.getUserId().toLowerCase());
        return signUpService.signUpUsers(params);

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
