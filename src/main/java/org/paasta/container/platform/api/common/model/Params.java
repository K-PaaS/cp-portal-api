package org.paasta.container.platform.api.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.paasta.container.platform.api.common.Constants;

@Data
public class Params {

    public String cluster = Constants.EMPTY_STRING;
    public String namespace = Constants.EMPTY_STRING;
    public String resourceName = Constants.EMPTY_STRING;
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

    // rest send type
    public Boolean isClusterToken = false;

    public
    @JsonProperty("yaml")
    String yaml = Constants.EMPTY_STRING;


    private String browser = Constants.EMPTY_STRING;
    private String clientIp = Constants.EMPTY_STRING;


    public Params(){}

    // sa, rb 관련 생성자
    public Params(String cluster, String namespace, String sa, String role, Boolean isClusterToken) {
        this.cluster = cluster;
        this.namespace = namespace;
        this.rs_sa = sa;
        this.rs_role = role;
        this.isClusterToken = isClusterToken;

    }


}