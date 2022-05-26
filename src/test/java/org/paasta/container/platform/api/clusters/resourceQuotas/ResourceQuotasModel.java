//package org.paasta.container.platform.api.clusters.resourceQuotas;
//
//import org.paasta.container.platform.api.clusters.resourceQuotas.support.ResourceQuotasConvertStatus;
//import org.paasta.container.platform.api.clusters.resourceQuotas.support.ResourceQuotasSpec;
//import org.paasta.container.platform.api.clusters.resourceQuotas.support.ResourceQuotasStatus;
//import org.paasta.container.platform.api.clusters.resourceQuotas.support.ResourceQuotasStatusItem;
//import org.paasta.container.platform.api.common.Constants;
//import org.paasta.container.platform.api.common.model.CommonMetaData;
//import org.paasta.container.platform.api.common.model.CommonStatusCode;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * ResourceQuotas Model 클래스
// *
// * @author kjhoon
// * @version 1.0
// * @since 2021.11.20
// **/
//public class ResourceQuotasModel {
//    private static final String NAMESPACE = "cp-namespace";
//    private static final String CREATION_TIME = "2020-11-17T09:31:37Z";
//    private static final String RESOURCE_QUOTA_NAME = "k8s-resource-quota";
//
//    public static CommonMetaData getMetadata() {
//        CommonMetaData metaData = new CommonMetaData();
//        metaData.setName(RESOURCE_QUOTA_NAME);
//        metaData.setNamespace(NAMESPACE);
//        metaData.setCreationTimestamp(CREATION_TIME);
//
//        return metaData;
//    }
//
//    public static ResourceQuotasSpec getSpec(){
//        ResourceQuotasSpec spec = new ResourceQuotasSpec();
//        Map<String, String> hard = new HashMap<>();
//        List<String> scopes = new ArrayList<>();
//
//        spec.setHard(hard);
//        spec.setScopes(scopes);
//
//        return spec;
//    }
//
//    public static ResourceQuotasStatus getStatus() {
//        ResourceQuotasStatus status = new ResourceQuotasStatus();
//        Map<String, String> hard = new HashMap<>();
//        hard.put("limits.cpu", "2");
//        hard.put("limits.memory", "2Gi");
//
//        Map<String, String> used = new HashMap<>();
//        used.put("limits.cpu", "0");
//        used.put("limits.memory", "0");
//
//        status.setHard(hard);
//        status.setUsed(used);
//
//        return status;
//    }
//
//    public static ResourceQuotasAdmin getResourceQuotasAdmin() {
//        ResourceQuotasAdmin resourceQuotasAdmin = new ResourceQuotasAdmin();
//
//        resourceQuotasAdmin.setName(RESOURCE_QUOTA_NAME);
//        resourceQuotasAdmin.setNamespace(NAMESPACE);
//        resourceQuotasAdmin.setScopes(new ArrayList<>());
//        resourceQuotasAdmin.setCreationTimestamp(CREATION_TIME);
//        resourceQuotasAdmin.setItems(getResourceQuotasStatusItemList());
//        resourceQuotasAdmin.setMetadata(getMetadata());
//        resourceQuotasAdmin.setSpec(getSpec());
//        resourceQuotasAdmin.setStatus(getStatus());
//
//        return resourceQuotasAdmin;
//    }
//
//    public static List<ResourceQuotasStatusItem> getResourceQuotasStatusItemList() {
//        List<ResourceQuotasStatusItem> itemList = new ArrayList<>();
//
//        ResourceQuotasStatusItem item1 = new ResourceQuotasStatusItem("limits.cpu", "8", "0");
//        ResourceQuotasStatusItem item2 = new ResourceQuotasStatusItem("limits.memory", "12Gi","0" );
//
//        itemList.add(item1);
//        itemList.add(item2);
//
//        return itemList;
//    }
//
//    public static List<ResourceQuotasListAdminItem> getResourceQuotasListAdminItems() {
//        List<ResourceQuotasListAdminItem> listAdminItems = new ArrayList<>();
//        ResourceQuotasListAdminItem item = new ResourceQuotasListAdminItem();
//        item.setName(RESOURCE_QUOTA_NAME);
//        item.setNamespace(NAMESPACE);
//        item.setCreationTimestamp(CREATION_TIME);
//        item.setConvertStatus(getConvertStatus());
//        item.setMetadata(getMetadata());
//        item.setStatus(getStatus());
//
//        listAdminItems.add(item);
//
//        return listAdminItems;
//
//    }
//
//    public static Map<String, Object> getConvertStatus() {
//        HashMap<String, Object> convertStatus = new HashMap<>();
//        ResourceQuotasConvertStatus resourceQuotasConvertStatusCpu = new ResourceQuotasConvertStatus();
//        resourceQuotasConvertStatusCpu.setHard("2");
//        resourceQuotasConvertStatusCpu.setUsed("0");
//
//        ResourceQuotasConvertStatus resourceQuotasConvertStatusMemory = new ResourceQuotasConvertStatus();
//        resourceQuotasConvertStatusMemory.setHard("2Gi");
//        resourceQuotasConvertStatusMemory.setUsed("0");
//
//        convertStatus.put("limits.cpu", resourceQuotasConvertStatusCpu);
//        convertStatus.put("limits.memory", resourceQuotasConvertStatusMemory);
//
//        return convertStatus;
//    }
//
//
//    public static ResourceQuotasListAdmin getResourceQuotasListAdmin() {
//        ResourceQuotasListAdmin resourceQuotasListAdmin = new ResourceQuotasListAdmin();
//
//        resourceQuotasListAdmin.setItems(getResourceQuotasListAdminItems());
//
//        return resourceQuotasListAdmin;
//    }
//
//    public static List<ResourceQuotasDefault> getResourceQuotasDefaultsFromDB() {
//        List<ResourceQuotasDefault> list = new ArrayList<>();
//        ResourceQuotasDefault quotasDefaultLow = new ResourceQuotasDefault();
//        quotasDefaultLow.setId("1");
//        quotasDefaultLow.setName("paas-ta-container-platform-low-rq");
//        quotasDefaultLow.setLimitCpu("2");
//        quotasDefaultLow.setLimitMemory("2Gi");
//        quotasDefaultLow.setRequestCpu("-");
//        quotasDefaultLow.setRequestMemory("-");
//        quotasDefaultLow.setStatus("{\"cpu\":{\"used\":\"200m\",\"hard\":\"2\"}, \"memory\":{\"used\":\"1800Mi\",\"hard\": \"2Gi\"}}");
//        quotasDefaultLow.setCreationTimestamp(CREATION_TIME);
//        quotasDefaultLow.setCheckYn("Y");
//
//        ResourceQuotasDefault quotasDefaultMedium = new ResourceQuotasDefault();
//        quotasDefaultMedium.setId("1");
//        quotasDefaultMedium.setName("paas-ta-container-platform-medium-rq");
//        quotasDefaultMedium.setLimitCpu("4");
//        quotasDefaultMedium.setLimitMemory("6Gi");
//        quotasDefaultMedium.setRequestCpu("-");
//        quotasDefaultMedium.setRequestMemory("-");
//        quotasDefaultMedium.setStatus("{\"cpu\":{\"used\":\"200m\",\"hard\":\"4\"}, \"memory\":{\"used\":\"1800Mi\",\"hard\": \"6Gi\"}}");
//        quotasDefaultMedium.setCreationTimestamp(CREATION_TIME);
//        quotasDefaultMedium.setCheckYn("Y");
//
//        ResourceQuotasDefault quotasDefaultHigh = new ResourceQuotasDefault();
//        quotasDefaultHigh.setId("1");
//        quotasDefaultHigh.setName("paas-ta-container-platform-high-rq");
//        quotasDefaultHigh.setLimitCpu("8");
//        quotasDefaultHigh.setLimitMemory("12Gi");
//        quotasDefaultHigh.setRequestCpu("-");
//        quotasDefaultHigh.setRequestMemory("-");
//        quotasDefaultHigh.setStatus("{\"cpu\":{\"used\":\"200m\",\"hard\":\"8\"}, \"memory\":{\"used\":\"1800Mi\",\"hard\": \"12Gi\"}}");
//        quotasDefaultHigh.setCreationTimestamp(CREATION_TIME);
//        quotasDefaultHigh.setCheckYn("Y");
//
//        list.add(quotasDefaultLow);
//        list.add(quotasDefaultMedium);
//        list.add(quotasDefaultHigh);
//
//        return list;
//    }
//
//    public static List<ResourceQuotasDefault> getResourceQuotasDefaults() {
//        List<ResourceQuotasDefault> list = new ArrayList<>();
//        ResourceQuotasDefault quotasDefaultLow = new ResourceQuotasDefault();
//        quotasDefaultLow.setId("1");
//        quotasDefaultLow.setName("paas-ta-container-platform-low-rq");
//        quotasDefaultLow.setLimitCpu("2");
//        quotasDefaultLow.setLimitMemory("2Gi");
//        quotasDefaultLow.setRequestCpu("-");
//        quotasDefaultLow.setRequestMemory("-");
//        quotasDefaultLow.setStatus("{\"cpu\":{\"used\":\"200m\",\"hard\":\"2\"}, \"memory\":{\"used\":\"1800Mi\",\"hard\": \"2Gi\"}}");
//        quotasDefaultLow.setCreationTimestamp(CREATION_TIME);
//        quotasDefaultLow.setCheckYn("Y");
//
//        ResourceQuotasDefault quotasDefaultMedium = new ResourceQuotasDefault();
//        quotasDefaultMedium.setId("1");
//        quotasDefaultMedium.setName("paas-ta-container-platform-medium-rq");
//        quotasDefaultMedium.setLimitCpu("4");
//        quotasDefaultMedium.setLimitMemory("6Gi");
//        quotasDefaultMedium.setRequestCpu("-");
//        quotasDefaultMedium.setRequestMemory("-");
//        quotasDefaultMedium.setStatus("{\"cpu\":{\"used\":\"200m\",\"hard\":\"4\"}, \"memory\":{\"used\":\"1800Mi\",\"hard\": \"6Gi\"}}");
//        quotasDefaultMedium.setCreationTimestamp(CREATION_TIME);
//        quotasDefaultMedium.setCheckYn("Y");
//
//        ResourceQuotasDefault quotasDefaultHigh = new ResourceQuotasDefault();
//        quotasDefaultHigh.setId("1");
//        quotasDefaultHigh.setName("paas-ta-container-platform-high-rq");
//        quotasDefaultHigh.setLimitCpu("8");
//        quotasDefaultHigh.setLimitMemory("12Gi");
//        quotasDefaultHigh.setRequestCpu("-");
//        quotasDefaultHigh.setRequestMemory("-");
//        quotasDefaultHigh.setStatus("{\"cpu\":{\"used\":\"200m\",\"hard\":\"8\"}, \"memory\":{\"used\":\"1800Mi\",\"hard\": \"12Gi\"}}");
//        quotasDefaultHigh.setCreationTimestamp(CREATION_TIME);
//        quotasDefaultHigh.setCheckYn("Y");
//
//        ResourceQuotasDefault k8sRq = new ResourceQuotasDefault();
//        k8sRq.setId(null);
//        k8sRq.setName(RESOURCE_QUOTA_NAME);
//        k8sRq.setLimitCpu("2");
//        k8sRq.setLimitMemory("2Gi");
//        k8sRq.setRequestCpu("0");
//        k8sRq.setRequestMemory("0");
//        k8sRq.setStatus("{\"cpu\":{\"used\":\"0\",\"hard\":\"2\"}, \"memory\":{\"used\":\"0\",\"hard\": \"2Gi\"}}");
//        k8sRq.setCreationTimestamp(CREATION_TIME);
//        k8sRq.setCheckYn("Y");
//
//        list.add(quotasDefaultLow);
//        list.add(quotasDefaultMedium);
//        list.add(quotasDefaultHigh);
//        list.add(k8sRq);
//
//        return list;
//    }
//
//    public static ResourceQuotasDefaultList getResourceQuotasDefaultList() {
//        ResourceQuotasDefaultList resourceQuotasDefaultList = new ResourceQuotasDefaultList();
//        resourceQuotasDefaultList.setItems(getResourceQuotasDefaultsFromDB());
//
//        return resourceQuotasDefaultList;
//    }
//
//    public static ResourceQuotasDefaultList getUpdateResourceQuotasDefaultList() {
//        ResourceQuotasDefaultList resourceQuotasDefaultList = new ResourceQuotasDefaultList();
//        resourceQuotasDefaultList.setItems(getResourceQuotasDefaults());
//
//        return resourceQuotasDefaultList;
//    }
//
//    public static ResourceQuotasDefaultList getFinalResourceQuotasDefaultList() {
//        ResourceQuotasDefaultList finalResourceQuotasDefaultList = getUpdateResourceQuotasDefaultList();
//        finalResourceQuotasDefaultList.setResultCode(Constants.RESULT_STATUS_SUCCESS);
//        finalResourceQuotasDefaultList.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
//        finalResourceQuotasDefaultList.setHttpStatusCode(CommonStatusCode.OK.getCode());
//        finalResourceQuotasDefaultList.setDetailMessage(CommonStatusCode.OK.getMsg());
//
//        return finalResourceQuotasDefaultList;
//    }
//}
