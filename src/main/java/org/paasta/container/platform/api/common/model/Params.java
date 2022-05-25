package org.paasta.container.platform.api.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.paasta.container.platform.api.common.Constants;

@Data
@AllArgsConstructor
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
    public String nodeName;

    // request parameter setting
    public String addParam;
    public String selectorType;

    //resource yaml
    public String rs_sa;
    public String rs_role;
    public String rs_rq;
    public String rs_lr;


    public
    @JsonProperty("yaml")
    String yaml;


    public Params() {
        this.cluster = "";
        this.namespace = "";
        this.resourceName = "";
        this.offset = 0;
        this.limit = 0;
        this.orderBy = "creationTime";
        this.order = "desc";
        this.searchName = "";
        this.yaml ="";
        this.ownerReferencesUid = "";
        this.ownerReferencesName = "";
        this.selector = "";
        this.type = "";
        this.userId = "";
        this.nodeName = "";
        this.addParam = "";
        this.selectorType = Constants.RESOURCE_NAMESPACE;
        this.rs_sa ="";
        this.rs_role ="";
        this.rs_rq="";
        this.rs_lr= "";
    }


}