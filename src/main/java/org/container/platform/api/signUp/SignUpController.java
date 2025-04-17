package org.container.platform.api.signUp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.container.platform.api.common.*;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.common.model.ResultStatus;
import org.container.platform.api.config.NoAuth;
import org.container.platform.api.exception.ResultStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Sign Up Controller 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.06.02
 **/
@Tag(name = "SignUpController v1")
@RestController
public class SignUpController {

    private final SignUpService signUpService;


    /**
     * Instantiates a new SignUp controller
     *
     * @param signUpService     the signUpService
     */
    @Autowired
    public SignUpController(SignUpService signUpService) {
        this.signUpService = signUpService;
    }


    /**
     * 회원가입(Sign Up)
     *
     * @param params the params
     * @return the resultStatus
     */
    @Operation(summary = "회원가입(Sign Up)", operationId = "signUpUsers")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
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



}
