package org.paasta.container.platform.api.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.common.model.CommonObjectReference;
import org.paasta.container.platform.api.common.model.CommonSpec;
import org.paasta.container.platform.api.common.model.CommonStatus;

/**
 * Events Admin Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.17
 */
@Data
public class EventsAdmin {

    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private int count;
    private String firstTimestamp;
    private String lastTimestamp;
    private String message;
    private Events.EventSource source;
    private String subObject;

    public String getFirstTimestamp() {
        return CommonUtils.procSetTimestamp(firstTimestamp);
    }
    public String getLastTimestamp() {
        return CommonUtils.procSetTimestamp(lastTimestamp);
    }

    @JsonIgnore
    private CommonMetaData metadata;

    @JsonIgnore
    private CommonObjectReference involvedObject;

    @Data
    public class EventSource {
        private String component;
        private String host;
    }

    public String getSubObject() {
        return involvedObject.getFieldPath();
    }
    public void setSubObject(String subObject) {
        this.subObject = subObject;
    }
}
