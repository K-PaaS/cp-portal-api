package org.paasta.container.platform.api.overview;

import net.minidev.json.JSONObject;
import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.users.Users;
import org.paasta.container.platform.api.users.UsersList;
import org.paasta.container.platform.api.users.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GlobalOverviewNewService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalOverviewNewService.class);


    private final RestTemplateService restTemplateService;
    private final PropertyService propertyService;
    private final CommonService commonService;
    private final UsersService usersService;




    public GlobalOverviewNewService(RestTemplateService restTemplateService, PropertyService propertyService,
                                    CommonService commonService, UsersService usersService) {
        this.restTemplateService = restTemplateService;
        this.propertyService = propertyService;
        this.commonService = commonService;
        this.usersService = usersService;
    }


    public GlobalOverview getGlobalOverview(Params params) {
        Map<String,Object> map = new HashMap<>();
        params.setIsGlobal(true);

        // 사용자와 맵핑된 클러스터 목록 조회
        UsersList mappingClustersList = usersService.getMappingClustersListByUser(params);

        List<String> mappingClustersIds = mappingClustersList.getItems().stream().map(Users::getClusterId).collect(Collectors.toList());


        map.put(propertyService.getCpMetricCollectorApiClustersKey(), mappingClustersIds);
        JSONObject clustersJson =  new JSONObject(map);
        GlobalOverview globalOverview = getClustersMetricCollector(params, clustersJson);

        List<String> normalCollectClustersList = globalOverview.getItems().stream().map(GlobalOverviewItems::getClusterId).collect(Collectors.toList());


        for(Users users : mappingClustersList.getItems()) {
         if(!normalCollectClustersList.contains(users.getClusterId())) {
             GlobalOverviewItems failedCollectClusters = new GlobalOverviewItems(Constants.RESULT_STATUS_FAIL, users.getClusterId(), users.getClusterName(), users.getClusterProviderType());
                globalOverview.getItems().add(failedCollectClusters);
         }
        }


        return (GlobalOverview) commonService.setResultModel(globalOverview, Constants.RESULT_STATUS_SUCCESS);

    }


    public GlobalOverview getClustersMetricCollector(Params params, JSONObject jsonObject){
        return restTemplateService.sendGlobal(Constants.TARGET_METRIC_COLLECTOR_API, propertyService.getCpMetricCollectorApiClustersGetUrl(),
                HttpMethod.POST, jsonObject, GlobalOverview.class, params);
    }

}

