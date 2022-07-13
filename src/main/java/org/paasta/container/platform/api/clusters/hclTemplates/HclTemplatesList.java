package org.paasta.container.platform.api.clusters.hclTemplates;

import lombok.Data;
import org.paasta.container.platform.api.common.model.CommonItemMetaData;

import java.util.List;

/**
 * HclTemplates List Model 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.06.30
 **/

@Data
public class HclTemplatesList {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private CommonItemMetaData itemMetaData;
    private List<HclTemplates> items;
}
