package org.paasta.container.platform.api.clusters.limitRanges;

import lombok.Data;

import java.util.List;

/**
 * LimitRanges Default List Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.10.26
 **/
@Data
public class LimitRangesDefaultList {
    private String resultCode;
    private String resultMessage;
    private Integer httpStatusCode;
    private String detailMessage;

    private List<LimitRangesDefault> items;
}
