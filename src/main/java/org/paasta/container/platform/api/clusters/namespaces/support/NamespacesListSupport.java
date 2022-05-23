package org.paasta.container.platform.api.clusters.namespaces.support;

import lombok.Data;

import java.util.List;

/**
 * Namespaces List Model for Select Box 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2020.10.14
 */
@Data
public class NamespacesListSupport{
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private List<String> items;
}
