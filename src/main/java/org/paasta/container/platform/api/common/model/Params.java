package org.paasta.container.platform.api.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

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


    @JsonProperty("yaml")
    String yaml;


    public Params() {
        this.cluster = "";
        this.namespace = "";
        this.resourceName = "";
        this.offset = 0;
        this.limit = 0;
        this.orderBy = "creationTime";
        this.order = "";
        this.searchName = "";
        this.yaml ="";
        this.ownerReferencesUid = "";
        this.ownerReferencesName = "";
        this.selector = "";
        this.type = "";
        this.userId = "";
    }


}
