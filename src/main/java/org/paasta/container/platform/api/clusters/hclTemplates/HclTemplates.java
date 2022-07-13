package org.paasta.container.platform.api.clusters.hclTemplates;

import lombok.Data;

/**
 * HclTemplates Model 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.06.30
 **/
@Data
public class HclTemplates {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private long id;
    private String name;
    private String provider;
    private String hclScript;
    private String region;
    private String created;
    private String lastModified;
}
