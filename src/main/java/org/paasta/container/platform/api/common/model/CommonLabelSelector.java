package org.paasta.container.platform.api.common.model;

import lombok.Data;

import java.util.Map;

/**
 * Common Label Selector Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.25
 */
@Data
public class CommonLabelSelector {
    private Map<String, String> matchLabels;
}
