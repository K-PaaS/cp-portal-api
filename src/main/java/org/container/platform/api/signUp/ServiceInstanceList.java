package org.container.platform.api.signUp;

import lombok.Data;
import java.util.List;

/**
 * ServiceInstance List Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2021.07.21
 **/
@Data
public class ServiceInstanceList {
    private String resultCode;
    private String resultMessage;

    private List<ServiceInstance> items;
}
