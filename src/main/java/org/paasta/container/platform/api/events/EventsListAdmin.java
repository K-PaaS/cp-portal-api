package org.paasta.container.platform.api.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.model.CommonMetaData;
import org.paasta.container.platform.api.events.support.EventInvolvedObject;
import org.paasta.container.platform.api.events.support.EventSource;

import java.util.List;

/**
 * Events List Admin Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.11.06
 */
@Data
public class EventsListAdmin {

    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private List<EventsAdminItem> items;
}


class EventsAdminItem {

    private String message;
    private EventSource source;
    private String filePath;
    private Integer count;
    private String firstTimestamp;
    private String lastTimestamp;

    @JsonIgnore
    private CommonMetaData metadata;
    @JsonIgnore
    private EventInvolvedObject involvedObject;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public EventSource getSource() {
        return source;
    }

    public void setSource(EventSource source) {
        this.source = source;
    }

    public String getFilePath() {
        return  CommonUtils.procReplaceNullValue(involvedObject.getFieldPath());
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getCount() {
        if(count == null) { count = 0; }
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getFirstTimestamp() {
        return CommonUtils.procReplaceNullValue(firstTimestamp);
    }

    public void setFirstTimestamp(String firstTimestamp) {
        this.firstTimestamp = firstTimestamp;
    }

    public String getLastTimestamp() {
        return CommonUtils.procReplaceNullValue(lastTimestamp);
    }

    public void setLastTimestamp(String lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    public CommonMetaData getMetadata() {
        return metadata;
    }

    public void setMetadata(CommonMetaData metadata) {
        this.metadata = metadata;
    }

    public EventInvolvedObject getInvolvedObject() {
        return involvedObject;
    }

    public void setInvolvedObject(EventInvolvedObject involvedObject) {
        this.involvedObject = involvedObject;
    }
}
