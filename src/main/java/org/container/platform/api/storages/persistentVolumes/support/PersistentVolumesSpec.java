package org.container.platform.api.storages.persistentVolumes.support;

import lombok.Data;
import org.container.platform.api.common.model.CommonLabelSelector;
import org.container.platform.api.common.model.CommonResourceRequirement;
import org.container.platform.api.common.model.CommonTypedLocalObjectReference;

import java.util.List;
import java.util.Map;

/**
 * PersistentVolumes Spec Model 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2020.10.19
 */
@Data
public class PersistentVolumesSpec {
    private List<String> accessModes;
    private String volumeName;
    private String storageClassName;
    private String volumeMode;
    private CommonTypedLocalObjectReference dataSource;
    private CommonResourceRequirement resources;
    private CommonLabelSelector selector;
    private ObjectReference claimRef;
    private Object capacity;
    private String persistentVolumeReclaimPolicy;

    // persistentVolume type

    private Map awsElasticBlockStore;
    private Map azureDisk;
    private Map azureFile;
    private Map cephfs;
    private Map cinder;
    private Map csi;
    private Map fc;
    private Map flexVolume;
    private Map flocker;
    private Map gcePersistentDisk;
    private Map glusterfs;
    private Map hostPath;
    private Map iscsi;
    private Map local;
    private Map nfs;
    private Map photonPersistentDisk;
    private Map portworxVolume;
    private Map quobyte;
    private Map rbd;
    private Map scaleIO;
    private Map storageos;
    private Map vsphereVolume;






}
