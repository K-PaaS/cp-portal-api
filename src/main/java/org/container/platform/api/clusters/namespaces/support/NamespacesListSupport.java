package org.container.platform.api.clusters.namespaces.support;

import lombok.Data;

import java.util.List;

/**
 * Namespaces List Model for Select Box 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.24
 */
@Data
public class NamespacesListSupport{
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private List<String> items;
}
