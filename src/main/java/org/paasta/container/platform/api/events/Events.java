package org.paasta.container.platform.api.events;

import lombok.Data;
import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.common.model.CommonObjectReference;

/**
 * Events Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.17
 */
@Data
public class Events {

    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private CommonMetaData metadata;
    private int count;
    private String firstTimestamp;
    private String lastTimestamp;
    private String message;
    private EventSource source;
    private String type;
    private CommonObjectReference involvedObject;

    public String getFirstTimestamp() {
        return CommonUtils.procSetTimestamp(firstTimestamp);
    }
    public String getLastTimestamp() {
        return CommonUtils.procSetTimestamp(lastTimestamp);
    }


    @Data
    public class EventSource {
        private String component;
        private String host;
    }
}
