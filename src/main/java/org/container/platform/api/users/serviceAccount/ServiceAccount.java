package org.container.platform.api.users.serviceAccount;

import lombok.Data;
import org.container.platform.api.common.model.CommonMetaData;

import java.util.List;
import java.util.Map;

/**
 * Service Account Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.12.07
 **/
@Data
public class ServiceAccount {
    private List<Map<String, String>> secrets;
    private CommonMetaData metadata;
}
