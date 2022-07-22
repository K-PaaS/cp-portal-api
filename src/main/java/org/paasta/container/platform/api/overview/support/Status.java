package org.paasta.container.platform.api.overview.support;

import lombok.Data;

@Data
public class Status {
    private String type;
    private long count;
    private long percent;


    public Status(){

    }

    public Status(String type, long count, long percent) {
        this.type = type;
        this.count = count;
        this.percent = percent;
    }
}
