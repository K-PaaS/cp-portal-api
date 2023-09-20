package org.container.platform.api.common.model;

import lombok.Data;

@Data
public class CommonNodeInfo {

    private String kernelVersion;
    private String containerRuntimeVersion;
    private String kubeletVersion;
    private String kubeProxyVersion;
}
