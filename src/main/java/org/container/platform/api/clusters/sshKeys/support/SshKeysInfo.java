package org.container.platform.api.clusters.sshKeys.support;

import lombok.Data;
import org.container.platform.api.common.Constants;

/**
 * SshKeysInfo Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.01.04
 **/
@Data
public class SshKeysInfo {
    private String privateKey = Constants.EMPTY_STRING;
}
