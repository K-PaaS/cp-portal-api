package org.paasta.container.platform.api.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Common Item Meta Data Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonItemMetaData {

    private Integer allItemCount;
    private Integer remainingItemCount;

}
