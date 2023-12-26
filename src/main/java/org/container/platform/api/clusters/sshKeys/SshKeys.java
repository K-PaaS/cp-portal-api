package org.container.platform.api.clusters.sshKeys;

import lombok.Data;

/**
 * SshKeys Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2023.12.26
 **/

@Data
public class SshKeys {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private long id;
    private String name;
    private String provider;
    private String privateKey;
    private String created;
    private String lastModified;
}
