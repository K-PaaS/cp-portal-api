package org.paasta.container.platform.api.login.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * login Meta Data Item Model 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.10.29
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class loginMetaDataItem {
    private String namespace;
    private String userType;
}
