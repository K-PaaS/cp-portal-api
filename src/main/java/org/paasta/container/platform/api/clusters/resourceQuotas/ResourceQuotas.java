package org.paasta.container.platform.api.clusters.resourceQuotas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import org.paasta.container.platform.api.clusters.resourceQuotas.support.ResourceQuotasSpec;
import org.paasta.container.platform.api.clusters.resourceQuotas.support.ResourceQuotasStatus;
import org.paasta.container.platform.api.common.CommonUtils;
import org.paasta.container.platform.api.common.model.CommonMetaData;

import java.util.Map;

/**
 * ResourceQuotas Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.03
 */
@Data
public class ResourceQuotas {
  private String resultCode;
  private String resultMessage;
  private Integer httpStatusCode;
  private String detailMessage;
  private String nextActionUrl;
  private String apiVersion;
  private String kind;
  private CommonMetaData metadata;
  private ResourceQuotasSpec spec;

  @JsonIgnore
  private ResourceQuotasStatus status;

  private Map<String, Object> resourceQuotasStatus;

  public String getNextActionUrl() {
    return CommonUtils.procReplaceNullValue(nextActionUrl);
  }
}
