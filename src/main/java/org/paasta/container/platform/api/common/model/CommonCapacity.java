package org.paasta.container.platform.api.common.model;

import lombok.Data;
import org.paasta.container.platform.api.metrics.custom.Quantity;

@Data
public class CommonCapacity {
    private Quantity cpu;
    private Quantity memory;
    private String pods;

}
