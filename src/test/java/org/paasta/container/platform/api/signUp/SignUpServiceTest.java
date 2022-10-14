package org.paasta.container.platform.api.signUp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.platform.api.accessInfo.AccessTokenService;
import org.paasta.container.platform.api.clusters.clusters.ClustersService;
import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.users.Users;
import org.paasta.container.platform.api.users.UsersList;
import org.paasta.container.platform.api.users.UsersService;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.paasta.container.platform.api.common.Constants.NULL_REPLACE_TEXT;
import static org.paasta.container.platform.api.common.Constants.TARGET_COMMON_API;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class SignUpServiceTest {

    private static Params gParams = null;
    private static UsersList gResultUsersListModel = null;
    private static Users gResultUsersModel = null;
    private static ResultStatus gResultStatus = null;
    private static HashMap gResultMap = null;

    @Mock
    PropertyService propertyService;
    @Mock
    RestTemplateService restTemplateService;
    @Mock
    CommonService commonService;
    @Mock
    AccessTokenService accessTokenService;
    @Mock
    UsersService usersService;
    @Mock
    ResourceYamlService resourceYamlService;
    @Mock
    ClustersService clustersService;
    @Mock
    ResultStatusService resultStatusService;
    @Mock
    SignUpService signUpServiceMock;

    @InjectMocks
    SignUpService signUpService;


    @Before
    public void setUp() throws Exception {
        gParams = new Params();
        gResultUsersListModel = new UsersList();
        gResultUsersModel = new Users();
        gResultStatus = new ResultStatus();
        gResultStatus.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gResultMap = new HashMap();
    }

    @Test
    public void signUpUsers() {
        when(restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_CHECK_USER_REGISTER
                        .replace("{userId:.+}", gParams.getUserId())
                        .replace("{userAuthId:.+}", gParams.getUserAuthId())
                        .replace("{userType:.+}", gParams.getUserType())
                , HttpMethod.GET, null, Map.class, new Params())).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, UsersList.class)).thenReturn(gResultUsersListModel);
        when(propertyService.getDefaultNamespace()).thenReturn("");
        when(restTemplateService.send(TARGET_COMMON_API, Constants.URI_COMMON_API_USER_SIGNUP, HttpMethod.POST, new Users(NULL_REPLACE_TEXT, "", gParams.getUserId(), gParams.getUserAuthId(),
                gParams.getUserType(), NULL_REPLACE_TEXT, gParams.getUserAuthId(), NULL_REPLACE_TEXT, NULL_REPLACE_TEXT), ResultStatus.class, new Params())).thenReturn(gResultStatus);
        when(commonService.setResultModel(gResultStatus, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultStatus);
        when(signUpServiceMock.sendSignUpUser(gResultUsersModel)).thenReturn(gResultStatus);

        System.out.println("gResultStatus = " + gResultStatus);

        signUpService.signUpUsers(gParams);

    }

}