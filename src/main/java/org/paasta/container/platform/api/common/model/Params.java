package org.paasta.container.platform.api.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.paasta.container.platform.api.common.Constants;

@Data
public class Params {


    public String cluster;
    public String namespace;
    public String resourceName;
    public int offset;
    public int limit;
    public String orderBy;
    public String order;
    public String searchName;
    public String ownerReferencesUid;
    public String ownerReferencesName;
    public String selector;
    public String type;
    public String userId;
    public String userAuthId;
    public String userType;
    public String isActive;
    public String nodeName;
    public String resourceUid;

    // request parameter setting
    public String addParam;
    public String selectorType;

    //resource yaml
    public String rs_sa;
    public String rs_role;
    public String rs_rq;
    public String rs_lr;

    // sign Up
    public Boolean isSuperAdmin;

    public
    @JsonProperty("yaml")
    String yaml;


    private String browser;
    private String clientIp;


    public Params() {
        this.cluster = Constants.EMPTY_STRING;
        this.namespace = Constants.EMPTY_STRING;
        this.resourceName = Constants.EMPTY_STRING;
        this.offset = 0;
        this.limit = 0;
        this.orderBy = "creationTime";
        this.order = "desc";
        this.searchName = Constants.EMPTY_STRING;
        this.yaml =Constants.EMPTY_STRING;
        this.ownerReferencesUid = Constants.EMPTY_STRING;
        this.ownerReferencesName = Constants.EMPTY_STRING;
        this.selector = Constants.EMPTY_STRING;
        this.type = Constants.EMPTY_STRING;
        this.userId = Constants.EMPTY_STRING;
        this.userAuthId = Constants.EMPTY_STRING;
        this.userType = Constants.EMPTY_STRING;
        this.isActive = "true";
        this.nodeName = Constants.EMPTY_STRING;
        this.resourceUid = Constants.EMPTY_STRING;
        this.addParam = Constants.EMPTY_STRING;
        this.selectorType = Constants.RESOURCE_NAMESPACE;
        this.rs_sa =Constants.EMPTY_STRING;
        this.rs_role =Constants.EMPTY_STRING;
        this.rs_rq=Constants.EMPTY_STRING;
        this.rs_lr= Constants.EMPTY_STRING;
        this.isSuperAdmin = false;
    }



    // sa, rb 관련 생성자
    public Params(String cluster, String namespace, String sa, String role) {
        this.cluster = cluster;
        this.namespace = namespace;
        this.rs_sa = sa;
        this.rs_role = role;

    }
}