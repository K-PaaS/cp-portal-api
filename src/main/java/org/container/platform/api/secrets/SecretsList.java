package org.container.platform.api.secrets;

import lombok.Data;
import org.container.platform.api.common.model.CommonItemMetaData;
import java.util.List;
import java.util.Map;

/**
 * Secrets List 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.07.31
 **/
@Data
public class SecretsList {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private Map metadata;
    private CommonItemMetaData itemMetaData;
    private List<Secrets> items;
}
