package org.container.platform.api.storages.storageClasses.support;

import lombok.Data;
/**
 * StorageClasses Item Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2020.10.28
 */
@Data
public class StorageClassesItem {
    private String provisioner;
    private Object parameters;
}
