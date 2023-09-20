package org.container.platform.api.clusters.clusters.support;

import lombok.Data;
import org.container.platform.api.common.Constants;

/**
 * GCPInfo Model 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.06.30
 **/
@Data
public class GCPInfo{
    private String auth_provider_x509_cert_url = Constants.EMPTY_STRING;
    private String auth_uri = Constants.EMPTY_STRING;
    private String client_email = Constants.EMPTY_STRING;
    private String client_id = Constants.EMPTY_STRING;
    private String client_x509_cert_url = Constants.EMPTY_STRING;
    private String private_key = Constants.EMPTY_STRING;
    private String private_key_id = Constants.EMPTY_STRING;
    private String project_id = Constants.EMPTY_STRING;
    private String token_uri = Constants.EMPTY_STRING;
    private String type = Constants.EMPTY_STRING;
    private String project_name = Constants.EMPTY_STRING;
}
