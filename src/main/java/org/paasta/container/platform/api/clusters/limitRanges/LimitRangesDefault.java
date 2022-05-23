package org.paasta.container.platform.api.clusters.limitRanges;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LimitRanges Default Model 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.10.26
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LimitRangesDefault {
    @JsonIgnore
    private String id;

    private String name;
    private String type;
    private String resource;
    private String min;
    private String max;
    private String defaultRequest;
    private String defaultLimit;
    private String creationTimestamp;
}
