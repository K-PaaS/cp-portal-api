package org.container.platform.api.secret;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.container.platform.api.common.model.CommonMetaData;
import org.container.platform.api.secret.support.TokenRequestStatus;

@Data
public class TokenRequest {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private String name;
    private String namespace;
    private String creationTimestamp;
    private String expirationTimestamp;
    private String token;


    @JsonIgnore
    private CommonMetaData metadata;
    @JsonIgnore
    private TokenRequestStatus status;


    public String getName() {
        return name = metadata.getName();
    }

    public String getNamespace() {
        return namespace = metadata.getNamespace();
    }

    public String getCreationTimestamp() {
        return creationTimestamp = metadata.getCreationTimestamp();
    }

    public String getExpirationTimestamp() {
        return expirationTimestamp = status.getExpirationTimestamp();
    }

    public String getToken() {
        return token = status.getToken();
    }
}
