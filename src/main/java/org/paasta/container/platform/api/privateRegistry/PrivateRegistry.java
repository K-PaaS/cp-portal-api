package org.paasta.container.platform.api.privateRegistry;

import lombok.Data;
import org.paasta.container.platform.api.common.CommonUtils;

/**
 * Private Registry 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.12.01
 */
@Data
public class PrivateRegistry {

    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private String nextActionUrl;

    private long seq;
    private String repositoryUrl;
    private String repositoryName;
    private String imageName;
    private String imageVersion;

    public String getNextActionUrl() {
        return CommonUtils.procReplaceNullValue(nextActionUrl);
    }
}
