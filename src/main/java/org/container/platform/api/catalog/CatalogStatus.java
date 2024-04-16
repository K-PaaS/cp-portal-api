package org.container.platform.api.catalog;

import lombok.Data;
import org.container.platform.api.common.CommonUtils;

@Data
public class CatalogStatus {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private Object itemMetaData;
    private Object items;

    public Object getItemMetaData() {
            return CommonUtils.procReplaceNullValue(itemMetaData);
    }
}
