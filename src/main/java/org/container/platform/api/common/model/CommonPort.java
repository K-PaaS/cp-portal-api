package org.container.platform.api.common.model;

import lombok.Data;
import org.container.platform.api.common.CommonUtils;

/**
 * Common Port Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.26
 */
@Data
public class CommonPort {
    private String name;
    private String port;
    private String protocol;
    private String targetPort;
    private String nodePort;


    public String getName() {
        return CommonUtils.procReplaceNullValue(name);
    }

    public String getPort() {
        return CommonUtils.procReplaceNullValue(port);
    }

    public String getProtocol() {
        return CommonUtils.procReplaceNullValue(protocol);
    }

    public String getTargetPort() {
        return CommonUtils.procReplaceNullValue(targetPort);
    }

    public String getNodePort() {
        return CommonUtils.procReplaceNullValue(nodePort);
    }
}
