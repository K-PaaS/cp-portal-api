package org.container.platform.api.clusters.sshKeys;

import lombok.Data;
import org.container.platform.api.common.model.CommonItemMetaData;

import java.util.List;

/**
 * SshKeys List Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2023.12.26
 **/
@Data
public class SshKeysList {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private CommonItemMetaData itemMetaData;
    private List<SshKeys> items;
}
