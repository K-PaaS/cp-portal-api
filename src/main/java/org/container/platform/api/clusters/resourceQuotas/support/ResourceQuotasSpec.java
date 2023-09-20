package org.container.platform.api.clusters.resourceQuotas.support;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * ResourceQuotas Spec Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.09.03
 */
@Data
public class ResourceQuotasSpec {

  private Map<String, String> hard;
  private List<String> scopes;

}

