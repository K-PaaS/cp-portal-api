package org.container.platform.api.secrets.vaultSecrets;

import lombok.Data;

import java.util.List;

/**
 * DatabaseConnections 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2024.10.04
 **/
@Data
public class DatabaseConnectionsData {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private List data;
}
