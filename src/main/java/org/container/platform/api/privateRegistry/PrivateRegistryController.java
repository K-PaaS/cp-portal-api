package org.container.platform.api.privateRegistry;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Private Registry Controller 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.12.01
 */
@Tag(name = "PrivateRegistryController v1")
@RestController
@RequestMapping("/privateRegistry")
public class PrivateRegistryController {

    private final PrivateRegistryService privateRegistryService;

    /**
     * Instantiates a new PrivateRegistry controller
     *
     * @param privateRegistryService the privateRegistry Service
     */
    @Autowired
    public PrivateRegistryController(PrivateRegistryService privateRegistryService) {
        this.privateRegistryService = privateRegistryService;
    }

    /**
     * Private Registry 조회(Get Private Registry)
     *
     * @param imageName the imageName
     * @return the private registry
     */
    @Operation(summary = " Private Registry 조회(Get Private Registry)", operationId = "getPrivateRegistry")
    @Parameter(name = "imageName", description = "imageName", required = true, schema=@Schema(implementation = String.class))
    @GetMapping(value = "/{imageName:.+}")
    public Object getPrivateRegistry(@PathVariable("imageName") String imageName) {
        return privateRegistryService.getPrivateRegistry(imageName);
    }
}
