package org.paasta.container.platform.api.privateRegistry;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
@Api(value = "PrivateRegistryController v1")
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
    @ApiOperation(value = " Private Registry 조회(Get Private Registry)", nickname = "getPrivateRegistry")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "imageName", value = "이미지 명", required = true, dataType = "string", paramType = "path"),
    })
    @GetMapping(value = "/{imageName:.+}")
    public Object getPrivateRegistry(@PathVariable("imageName") String imageName) {
        return privateRegistryService.getPrivateRegistry(imageName);
    }
}
