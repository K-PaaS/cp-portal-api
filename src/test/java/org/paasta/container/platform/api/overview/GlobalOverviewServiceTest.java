package org.paasta.container.platform.api.overview;

import net.minidev.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.platform.api.common.CommonService;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.PropertyService;
import org.paasta.container.platform.api.common.RestTemplateService;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.users.Users;
import org.paasta.container.platform.api.users.UsersList;
import org.paasta.container.platform.api.users.UsersService;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class GlobalOverviewServiceTest {

    private static HashMap gResultMap = null;
    private static Params gParams = null;
    private static UsersList gResultUsersListModel = null;
    private static GlobalOverview gResultModel = null;

    @Mock
    RestTemplateService restTemplateService;
    @Mock
    PropertyService propertyService;
    @Mock
    CommonService commonService;
    @Mock
    UsersService usersService;

    @InjectMocks
    GlobalOverviewService globalOverviewService;

    @Before
    public void setUp() throws Exception {
        gResultMap = new HashMap();
        gParams = new Params();
        gResultUsersListModel = new UsersList();
        gResultModel = new GlobalOverview();
        List<GlobalOverviewItems> globalOverviewItems = new ArrayList<>();
        GlobalOverviewItems globalOverviewItem = new GlobalOverviewItems();
        globalOverviewItem.setClusterId("test");
        globalOverviewItems.add(globalOverviewItem);
        gResultModel.setItems(globalOverviewItems);

        List<Users> usersList = new ArrayList<>();
        Users user = new Users();
        user.setClusterId("test");
        usersList.add(user);

        gResultUsersListModel.setItems(usersList);
    }

    @Test
    public void getGlobalOverview() {
        gParams.setIsGlobal(true);

        // 사용자와 맵핑된 클러스터 목록 조회
        when(usersService.getMappingClustersListByUser(gParams)).thenReturn(gResultUsersListModel);
        when(propertyService.getCpMetricCollectorApiClustersKey()).thenReturn("test");
        Map<String,Object> map = new HashMap<>();
        List<String> stringList = new ArrayList<>();
        stringList.add("test");
        map.put("test", stringList);
        when(restTemplateService.sendGlobal(Constants.TARGET_METRIC_COLLECTOR_API, propertyService.getCpMetricCollectorApiClustersGetUrl(),
                HttpMethod.POST, new JSONObject(map), GlobalOverview.class, gParams)).thenReturn(gResultModel);

        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultModel);

        globalOverviewService.getGlobalOverview(gParams);
    }

}