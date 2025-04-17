package org.container.platform.api.clusters.cloudAccounts;

import lombok.Data;

/**
 * CloudAccounts Model 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.06.30
 **/
@Data
public class CloudAccounts {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private long id;
    private String name;
    private String provider;
    private String region;
    private String project;
    private String created;
    private String lastModified;
    private Object providerInfo;
    private String site;
}
