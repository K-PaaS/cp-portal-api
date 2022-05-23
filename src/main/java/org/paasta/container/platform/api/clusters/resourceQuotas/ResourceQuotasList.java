package org.paasta.container.platform.api.clusters.resourceQuotas;

import lombok.Data;
import org.paasta.container.platform.api.common.model.CommonItemMetaData;

import java.util.List;

/**
 * ResourceQuotas List Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.03
 */
@Data
public class ResourceQuotasList {

  private String resultCode;
  private String resultMessage;
  private Integer httpStatusCode;
  private String detailMessage;
  private CommonItemMetaData itemMetaData;
  private List<ResourceQuotas> items;

}

