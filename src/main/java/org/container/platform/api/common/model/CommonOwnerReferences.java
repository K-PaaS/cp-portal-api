package org.container.platform.api.common.model;

import lombok.Data;

/**
 * Common Owner References Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.25
 */
@Data
public
class CommonOwnerReferences {
    private String name;
    private String uid;
    private boolean controller;
}