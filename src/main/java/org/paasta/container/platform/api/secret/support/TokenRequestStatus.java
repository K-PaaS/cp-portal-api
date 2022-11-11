package org.paasta.container.platform.api.secret.support;

import lombok.Data;

@Data
public class TokenRequestStatus {

    private String expirationTimestamp;
    private String token;
}
