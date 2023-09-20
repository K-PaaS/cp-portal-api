package org.container.platform.api.signUp;

import lombok.Data;

/**
 * ServiceInstance Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2021.07.21
 */

@Data
public class ServiceInstance {
    private String serviceInstanceId;
    private String userId;
    private String namespace;
    private String organizationGuid;
}

