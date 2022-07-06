package org.paasta.container.platform.api.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.paasta.container.platform.api.common.Constants;

import java.util.Collections;
import java.util.List;

@Data
public class Params {

    public String cluster = Constants.EMPTY_STRING;
    public String namespace = Constants.EMPTY_STRING;
    public String resource;
    public String resourceName = Constants.EMPTY_STRING;
    public String metadataName;
    public int offset = 0;
    public int limit = 0;
    public String orderBy = "creationTime";
    public String order = "desc";
    public String searchName = Constants.EMPTY_STRING;
    public String ownerReferencesUid = Constants.EMPTY_STRING;
    public String ownerReferencesName = Constants.EMPTY_STRING;
    public String selector = Constants.EMPTY_STRING;
    public String type = Constants.EMPTY_STRING;
    public String userId = Constants.EMPTY_STRING;
    public String userAuthId = Constants.EMPTY_STRING;
    public String userType = Constants.EMPTY_STRING;
    public String isActive = "true";
    public String nodeName = Constants.EMPTY_STRING;
    public String resourceUid = Constants.EMPTY_STRING;

    // request parameter setting
    public String addParam = Constants.EMPTY_STRING;
    public String selectorType = Constants.EMPTY_STRING;

    //resource yaml
    public String rs_sa = Constants.EMPTY_STRING;
    public String rs_role = Constants.EMPTY_STRING;
    public String rs_rq = Constants.EMPTY_STRING;
    public String rs_lr = Constants.EMPTY_STRING;

    // sign Up
    public Boolean isSuperAdmin = false;

    //ingress
    public List rules;

    // rest send type
    public Boolean isClusterToken = false;

    public
    @JsonProperty("yaml")
    String yaml = Constants.EMPTY_STRING;


    private String browser = Constants.EMPTY_STRING;
    private String clientIp = Constants.EMPTY_STRING;

    private Boolean isGlobal = false;

    public Params(){
        this.cluster = Constants.EMPTY_STRING;
        this.namespace = Constants.EMPTY_STRING;
        this.resource = Constants.EMPTY_STRING;
        this.resourceName = Constants.EMPTY_STRING;
        this.metadataName = Constants.EMPTY_STRING;
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
        this.rs_sa = Constants.EMPTY_STRING;
        this.rs_role = Constants.EMPTY_STRING;
        this.rs_rq = Constants.EMPTY_STRING;
        this.rs_lr = Constants.EMPTY_STRING;
        this.isSuperAdmin = false;
        this.rules = Collections.emptyList();
    }

    // sa, rb 관련 생성자
    public Params(String cluster, String namespace, String sa, String role, Boolean isClusterToken) {
        this.cluster = cluster;
        this.namespace = namespace;
        this.rs_sa = sa;
        this.rs_role = role;
        this.isClusterToken = isClusterToken;

    }


}