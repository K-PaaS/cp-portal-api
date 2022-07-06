package org.paasta.container.platform.api.storages.persistentVolumeClaims;

import java.util.List;
import java.util.Map;
import lombok.Data;
import org.paasta.container.platform.api.common.model.CommonItemMetaData;
import org.paasta.container.platform.api.storages.persistentVolumeClaims.support.PersistentVolumeClaimsListItem;


/**
 * PersistentVolumeClaims List Model 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.05.24
 */
@Data
public class PersistentVolumeClaimsList {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;
    private Map metadata;
    private CommonItemMetaData itemMetaData;
    private List<PersistentVolumeClaimsListItem> items;

}