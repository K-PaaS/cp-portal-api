package org.container.platform.api.events;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.container.platform.api.common.model.Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Events Controller 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.25
 */
@Tag(name = "EventsController v1")
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
     * 특정 Namespace 의 전체 Events 목록 조회(Get Events List in a Namespace)
     *
     * @param params the params
     * @return the events list
     */
    @Operation(summary = "특정 Namespace 의 전체 Events 목록 조회(Get Events list in a Namespace)", operationId = "getNamespaceEventsList")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping
    public Object getNamespaceEventsList(Params params) {
            return eventsService.getNamespaceEventsList(params);
    }


    /**
     * Events 목록 조회(Get Events list)
     *
     * @param params the params
     * @return the events list
     */
    @Operation(summary = "Events 목록 조회(Get Events list)", operationId = "getEventsList")
    @Parameter(name = "params", description = "request parameters", required = true, schema=@Schema(implementation = Params.class))
    @GetMapping(value = "/resources/{resourceUid:.+}")
    public EventsList getEventsList(Params params) {
            return eventsService.getEventsList(params);
    }

}
