package org.paasta.container.platform.api.storages.persistentVolumes.support;

public enum PersistentVolumeType {

    awsElasticBlockStore ("AWSElasticBlockStore"),
    azureDisk( "AzureDisk"),
    azureFile("AzureFile"),
    cephfs("CephFS"),
    cinder("Cinder"),
    csi("CSI"),
    fc("FC"),
    flexVolume("FlexVolume"),
    flocker( "Flocker"),
    gcePersistentDisk("GCEPersistentDisk"),
    glusterfs("Glusterfs"),
    hostPath("HostPath"),
    iscsi("ISCSI"),
    local("Local"),
    nfs("NFS"),
    photonPersistentDisk("PhotonPersistentDisk"),
    portworxVolume("PortworxVolume"),
    quobyte("Quobyte"),
    rbd("RBD"),
    scaleIO("ScaleIO"),
    storageos("StorageOS"),
    vsphereVolume("VsphereVolume");


    private String name;
    public String getName() {
        return (name);
    }

    PersistentVolumeType(String name) {
        this.name = name;
    }
}
