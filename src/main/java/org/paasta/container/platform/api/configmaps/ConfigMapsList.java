package org.paasta.container.platform.api.configmaps;

import lombok.Data;
import org.paasta.container.platform.api.common.model.CommonItemMetaData;

import java.util.List;
import java.util.Map;

/**
 * ConfigMaps List 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.05.25
 **/
@Data
public class ConfigMapsList {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private Map metadata;
    private List<ConfigMaps> items;
    private CommonItemMetaData itemMetaData;
}
