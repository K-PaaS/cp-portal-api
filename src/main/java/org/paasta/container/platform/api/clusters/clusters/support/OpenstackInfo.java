package org.paasta.container.platform.api.clusters.clusters.support;

import lombok.Data;

/**
 * OpenstackInfo Model 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.06.30
 **/
@Data
public class OpenstackInfo {
    String auth_url;
    String password;
    String user_name;
}
