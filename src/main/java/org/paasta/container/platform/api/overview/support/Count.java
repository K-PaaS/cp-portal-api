package org.paasta.container.platform.api.overview.support;

import lombok.Data;

@Data
public class Count {

    private Integer count;
    private Integer all;

    public Count(Integer count, Integer all){
        this.count = count;
        this.all = all;
    }
}
