package org.paasta.container.platform.api.events;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.paasta.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Events Controller 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.24
 */
@RestController
@RequestMapping("/clusters/{cluster:.+}/namespaces/{namespace:.+}/events")
public class EventsController {

    private final EventsService eventsService;

    /**
     * Instantiates a new Events controller
     *
     * @param eventsService the events service
     */
    @Autowired
    public EventsController(EventsService eventsService) {
        this.eventsService = eventsService;
    }


    /**
     * 특정 Namespace 의 전체 Events 목록 조회(Get Events list in a Namespace)
     *
     * @param params the params
     * @return the events list
     */
    @ApiOperation(value = "특정 Namespace 의 전체 Events 목록 조회(Get Events list in a Namespace)", nickname = "getNamespaceEventsList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping
    public EventsList getNamespaceEvents(Params params) {
        return eventsService.getNamespaceEventsList(params);
    }

    /**
     * Events 목록 조회(Get Events list)
     *
     * @param params the params
     * @return the events list
     */
    @ApiOperation(value = "Events 목록 조회(Get Events list)", nickname = "getEventsList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params", value = "request parameters", required = true, dataType = "common.model.Params", paramType = "body")
    })
    @GetMapping(value = "/resources/{ownerReferencesUid:.+}")
    public EventsList getEventsList(Params params) {
        return eventsService.getEventsList(params);
    }

}
