package org.container.platform.api.common.model;

import lombok.Data;

/**
 * Common Object Reference Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.25
 */
@Data
public class CommonObjectReference {
    private String apiVersion;
    private String fieldPath;
    private String kind;
    private String name;
    private String namespace;
    private String resourceVersion;
    private String uid;
}
