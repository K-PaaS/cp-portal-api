package org.paasta.container.platform.api.clusters.limitRanges;

import com.google.gson.internal.LinkedTreeMap;
import org.paasta.container.platform.api.clusters.limitRanges.support.LimitRangesItem;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.model.CommonItemMetaData;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.common.model.CommonSpec;
import org.paasta.container.platform.api.common.model.CommonStatusCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * LimitRanges Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.11.20
 **/
public class LimitRangesModel {
    private static final String NAMESPACE = "cp-namespace";
    private static final String LOW_LIMIT_NAME = "paas-ta-container-platform-low-limit-range";
    private static final String CREATION_TIME = "2020-11-17T09:31:37Z";

    private static final String LIMIT_RANGE_NAME = "paas-ta-container-platform-low-limit-range";
    private static final String LIMIT_RANGE_RESOURCE = "memory";
    private static final String LIMIT_RANGE_TYPE = "Container";
    private static final String LIMIT_RANGE_DEFAULT_LIMIT = "500Mi";
    private static final String LIMIT_RANGE_DEFAULT_REQUEST = "100Mi";
    private static final String MAX = "100Mi";
    private static final String MIN = "100Mi";

    private static final String K8S_LIMIT_NAME = "k8s-limit-range";
    private static final String LIMIT_RANGE_TYPE_PVC = "PersistentVolumeClaim";
    private static final String LIMIT_RANGE_RESOURCE_STORAGE = "storage";
    private static final String LIMIT_RANGE_DEFAULT_LIMIT_STORAGE = "-";
    private static final String LIMIT_RANGE_DEFAULT_REQUEST_STORAGE = "-";
    private static final String MAX_STORAGE = "2Gi";
    private static final String MIN_STORAGE = "1Gi";

    private static final String K8S_LIMIT_NAME_POD = "k8s-limit-range-pod";
    private static final String LIMIT_RANGE_TYPE_POD = "Pod";
    private static final String LIMIT_RANGE_RESOURCE_CPU = "cpu";
    private static final String LIMIT_RANGE_DEFAULT_LIMIT_CPU = "1Gi";
    private static final String LIMIT_RANGE_LIMIT_REQUEST_CPU = "300Mi";
    private static final String MAX_CPU = "700Mi";
    private static final String MIN_CPU = "100Mi";

    public static final String CHECK_Y = "Y";
    public static final String CHECK_N = "N";

    public static CommonMetaData getMetadata() {
        CommonMetaData metaData = new CommonMetaData();
        metaData.setName(LOW_LIMIT_NAME);
        metaData.setNamespace(NAMESPACE);
        metaData.setCreationTimestamp(CREATION_TIME);

        return metaData;
    }

    public static CommonMetaData getMetadata_k8s() {
        CommonMetaData metaData = new CommonMetaData();
        metaData.setName(K8S_LIMIT_NAME);
        metaData.setNamespace(NAMESPACE);
        metaData.setCreationTimestamp(CREATION_TIME);

        return metaData;
    }

    public static CommonSpec getSpec(){
        CommonSpec spec = new CommonSpec();
        spec.setLimits(getLimitsList());

        return spec;
    }

    public static CommonSpec getSpec_k8s(){
        CommonSpec spec = new CommonSpec();
        spec.setLimits(getLimitsListK8s());

        return spec;
    }

    public static LimitRangesItem getLimitRangesItem() {
        LimitRangesItem item = new LimitRangesItem();

        LinkedTreeMap defaultLimitMap = new LinkedTreeMap();
        defaultLimitMap.put(Constants.SUPPORTED_RESOURCE_MEMORY, "500Mi");

        LinkedTreeMap defaultRequestMap = new LinkedTreeMap();
        defaultRequestMap.put(Constants.SUPPORTED_RESOURCE_MEMORY, "100Mi");

        LinkedTreeMap max = new LinkedTreeMap();
        max.put(Constants.SUPPORTED_RESOURCE_MEMORY, "100Mi");

        LinkedTreeMap min = new LinkedTreeMap();
        min.put(Constants.SUPPORTED_RESOURCE_MEMORY, "100Mi");

        item.setType(Constants.LIMIT_RANGE_TYPE_CONTAINER);
        item.setResource(Constants.SUPPORTED_RESOURCE_MEMORY);
        item.setDefaultLimit(defaultLimitMap);
        item.setDefaultRequest(defaultRequestMap);
        item.setMax(max);
        item.setMin(min);

        return item;
    }

    public static LimitRangesItem getLimitRangesItem_k8s_pvc() {
        LimitRangesItem item = new LimitRangesItem();

        LinkedTreeMap defaultLimitMap = new LinkedTreeMap();
        defaultLimitMap.put(Constants.SUPPORTED_RESOURCE_STORAGE, "-");

        LinkedTreeMap defaultRequestMap = new LinkedTreeMap();
        defaultRequestMap.put(Constants.SUPPORTED_RESOURCE_STORAGE, "-");

        LinkedTreeMap max = new LinkedTreeMap();
        max.put(Constants.SUPPORTED_RESOURCE_STORAGE, "2Gi");

        LinkedTreeMap min = new LinkedTreeMap();
        min.put(Constants.SUPPORTED_RESOURCE_STORAGE, "1Gi");

        item.setType(Constants.LIMIT_RANGE_TYPE_PVC);
        item.setResource(Constants.SUPPORTED_RESOURCE_STORAGE);
        item.setDefaultLimit(defaultLimitMap);
        item.setDefaultRequest(defaultRequestMap);
        item.setMax(max);
        item.setMin(min);

        return item;
    }

    public static LimitRangesItem getLimitRangesItem_k8s_pod() {
        LimitRangesItem item = new LimitRangesItem();

        LinkedTreeMap defaultLimitMap = new LinkedTreeMap();
        defaultLimitMap.put(Constants.SUPPORTED_RESOURCE_CPU, "1Gi");

        LinkedTreeMap defaultRequestMap = new LinkedTreeMap();
        defaultRequestMap.put(Constants.SUPPORTED_RESOURCE_CPU, "300Mi");

        LinkedTreeMap max = new LinkedTreeMap();
        max.put(Constants.SUPPORTED_RESOURCE_CPU, "700Mi");

        LinkedTreeMap min = new LinkedTreeMap();
        min.put(Constants.SUPPORTED_RESOURCE_CPU, "100Mi");

        item.setType(Constants.LIMIT_RANGE_TYPE_POD);
        item.setResource(Constants.SUPPORTED_RESOURCE_CPU);
        item.setDefaultLimit(defaultLimitMap);
        item.setDefaultRequest(defaultRequestMap);
        item.setMax(max);
        item.setMin(min);

        return item;
    }

    public static List<LimitRangesItem> getLimitsList() {
        List<LimitRangesItem> limitsList = new ArrayList<>();
        limitsList.add(getLimitRangesItem());

        return limitsList;
    }

    public static List<LimitRangesItem> getLimitsListK8s() {
        List<LimitRangesItem> limitsList = new ArrayList<>();
        limitsList.add(getLimitRangesItem_k8s_pvc());
        limitsList.add(getLimitRangesItem_k8s_pod());

        return limitsList;
    }

    public static LimitRangesAdmin getLimitRangesAdminContainerMemory() {
        LimitRangesAdmin limitRangesAdmin = new LimitRangesAdmin();

        limitRangesAdmin.setName(getMetadata().getName());
        limitRangesAdmin.setCreationTimestamp(CREATION_TIME);
        limitRangesAdmin.setMetadata(getMetadata());
        limitRangesAdmin.setSpec(getSpec());

        return limitRangesAdmin;
    }

    public static LimitRangesListAdmin getLimitRangesListAdmin() {
        LimitRangesListAdmin finalResultListAdminModel = new LimitRangesListAdmin();

        LimitRangesListAdminItem listAdminItem = new LimitRangesListAdminItem();
        listAdminItem.setMetadata(getMetadata());
        listAdminItem.setSpec(getSpec());
        listAdminItem.setName(LOW_LIMIT_NAME);
        listAdminItem.setNamespace(NAMESPACE);
        listAdminItem.setCreationTimestamp(CREATION_TIME);

        LimitRangesListAdminItem k8sListAdminItem = new LimitRangesListAdminItem();
        k8sListAdminItem.setMetadata(getMetadata_k8s());
        k8sListAdminItem.setSpec(getSpec_k8s());
        k8sListAdminItem.setName(K8S_LIMIT_NAME);
        k8sListAdminItem.setNamespace(NAMESPACE);
        k8sListAdminItem.setCreationTimestamp(CREATION_TIME);

        List<LimitRangesListAdminItem> items = new ArrayList<>();
        items.add(listAdminItem);
        items.add(k8sListAdminItem);

        finalResultListAdminModel.setItems(items);
        finalResultListAdminModel.setMetadata(new LinkedTreeMap());
        finalResultListAdminModel.setItemMetaData(new CommonItemMetaData());

        return finalResultListAdminModel;
    }


    public static LimitRangesTemplateItem getLimitRangesTemplateItem() {
        LimitRangesTemplateItem item = new LimitRangesTemplateItem();
        CommonMetaData metadata = new CommonMetaData();

        item.setName(getLimitRangesDefault().getName());
        item.setType(getLimitRangesDefault().getType());
        item.setResource(getLimitRangesDefault().getResource());
        item.setMin(getLimitRangesDefault().getMin());
        item.setMax(getLimitRangesDefault().getMax());
        item.setDefaultRequest(getLimitRangesDefault().getDefaultRequest());
        item.setDefaultLimit(getLimitRangesDefault().getDefaultLimit());
        item.setCheckYn(CHECK_Y);
        metadata.setCreationTimestamp(CREATION_TIME);
        item.setMetadata(metadata);
        item.setCreationTimestamp(CREATION_TIME);

        return item;
    }

    public static LimitRangesTemplateItem getLimitRangesTemplateItemFromK8s_PVC() {
        LimitRangesTemplateItem item = new LimitRangesTemplateItem();
        CommonMetaData metadata = new CommonMetaData();

        item.setName(getLimitRangesDefault_K8s_PVC().getName());
        item.setType(getLimitRangesDefault_K8s_PVC().getType());
        item.setResource(getLimitRangesDefault_K8s_PVC().getResource());
        item.setMin(getLimitRangesDefault_K8s_PVC().getMin());
        item.setMax(getLimitRangesDefault_K8s_PVC().getMax());
        item.setDefaultRequest(getLimitRangesDefault_K8s_PVC().getDefaultRequest());
        item.setDefaultLimit(getLimitRangesDefault_K8s_PVC().getDefaultLimit());
        item.setCheckYn(CHECK_Y);
        metadata.setCreationTimestamp(CREATION_TIME);
        item.setMetadata(metadata);
        item.setCreationTimestamp(CREATION_TIME);

        return item;
    }

    public static LimitRangesTemplateItem getLimitRangesTemplateItemFromK8s_POD() {
        LimitRangesTemplateItem item = new LimitRangesTemplateItem();
        CommonMetaData metadata = new CommonMetaData();

        item.setName(getLimitRangesDefault_K8s_POD().getName());
        item.setType(getLimitRangesDefault_K8s_POD().getType());
        item.setResource(getLimitRangesDefault_K8s_POD().getResource());
        item.setMin(getLimitRangesDefault_K8s_POD().getMin());
        item.setMax(getLimitRangesDefault_K8s_POD().getMax());
        item.setDefaultRequest(getLimitRangesDefault_K8s_POD().getDefaultRequest());
        item.setDefaultLimit(getLimitRangesDefault_K8s_POD().getDefaultLimit());
        item.setCheckYn(CHECK_Y);
        metadata.setCreationTimestamp(CREATION_TIME);
        item.setMetadata(metadata);
        item.setCreationTimestamp(CREATION_TIME);

        return item;
    }

    public static LimitRangesDefault getLimitRangesDefault() {
        LimitRangesDefault limitRangesDefault = new LimitRangesDefault();
        limitRangesDefault.setName(LIMIT_RANGE_NAME);
        limitRangesDefault.setType(LIMIT_RANGE_TYPE);
        limitRangesDefault.setResource(LIMIT_RANGE_RESOURCE);
        limitRangesDefault.setDefaultLimit(LIMIT_RANGE_DEFAULT_LIMIT);
        limitRangesDefault.setDefaultRequest(LIMIT_RANGE_DEFAULT_REQUEST);
        limitRangesDefault.setMax(MAX);
        limitRangesDefault.setMin(MIN);
        limitRangesDefault.setCreationTimestamp(CREATION_TIME);

        return limitRangesDefault;
    }

    public static LimitRangesDefault getLimitRangesDefault_K8s_PVC() {
        LimitRangesDefault limitRangesDefault = new LimitRangesDefault();
        limitRangesDefault.setName(K8S_LIMIT_NAME);
        limitRangesDefault.setType(LIMIT_RANGE_TYPE_PVC);
        limitRangesDefault.setResource(LIMIT_RANGE_RESOURCE_STORAGE);
        limitRangesDefault.setDefaultLimit(LIMIT_RANGE_DEFAULT_LIMIT_STORAGE);
        limitRangesDefault.setDefaultRequest(LIMIT_RANGE_DEFAULT_REQUEST_STORAGE);
        limitRangesDefault.setMax(MAX_STORAGE);
        limitRangesDefault.setMin(MIN_STORAGE);
        limitRangesDefault.setCreationTimestamp(CREATION_TIME);

        return limitRangesDefault;
    }

    public static LimitRangesDefault getLimitRangesDefault_K8s_POD() {
        LimitRangesDefault limitRangesDefault = new LimitRangesDefault();
        limitRangesDefault.setName(K8S_LIMIT_NAME_POD);
        limitRangesDefault.setType(LIMIT_RANGE_TYPE_POD);
        limitRangesDefault.setResource(LIMIT_RANGE_RESOURCE_CPU);
        limitRangesDefault.setDefaultLimit(LIMIT_RANGE_DEFAULT_LIMIT_CPU);
        limitRangesDefault.setDefaultRequest(LIMIT_RANGE_LIMIT_REQUEST_CPU);
        limitRangesDefault.setMax(MAX_CPU);
        limitRangesDefault.setMin(MIN_CPU);
        limitRangesDefault.setCreationTimestamp(CREATION_TIME);

        return limitRangesDefault;
    }

    public static List<LimitRangesTemplateItem> getLimitRangesTemplateItemList() {
        List<LimitRangesTemplateItem> serversItemList = new ArrayList<>();
        serversItemList.add(LimitRangesModel.getLimitRangesTemplateItem());
        serversItemList.add(LimitRangesModel.getLimitRangesTemplateItemFromK8s_PVC());
        serversItemList.add(LimitRangesModel.getLimitRangesTemplateItemFromK8s_POD());

        return serversItemList;
    }


    public static LimitRangesTemplateList getLimitRangesTemplateList() {
        LimitRangesTemplateList templateList = new LimitRangesTemplateList();
        templateList.setItems(getLimitRangesTemplateItemList());
        templateList.setItemMetaData(new CommonItemMetaData());
        templateList.setMetadata(new HashMap());

        return templateList;
    }

    public static LimitRangesTemplateList getFinalLimitRangesTemplateList() {
        LimitRangesTemplateList templateList = getLimitRangesTemplateList();
        templateList.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        templateList.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        templateList.setHttpStatusCode(CommonStatusCode.OK.getCode());
        templateList.setDetailMessage(CommonStatusCode.OK.getMsg());

        return templateList;
    }
}
