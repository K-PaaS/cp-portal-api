package org.paasta.container.platform.api.overview.support;

import lombok.Data;

@Data
public class Count {

    private Object count;
    private Object all;

    public Count(Object count, Object all){
        this.count = count;
        this.all = all;
    }
}
