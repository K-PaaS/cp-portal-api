package org.paasta.container.platform.api.events;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Events Controller 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2020.11.05
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
     * @param cluster    the cluster
     * @param namespace  the namespace
     * @param isAdmin    the isAdmin
     * @return the events list
     */
    @ApiOperation(value = "특정 Namespace 의 전체 Events 목록 조회(Get Events list in a Namespace)", nickname = "getNamespaceEventsList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
    })
    @GetMapping
    public Object getNamespaceEventsList(@PathVariable(value = "cluster") String cluster,
                                         @PathVariable(value = "namespace") String namespace,
                                         @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        if (isAdmin) {
            return eventsService.getNamespaceEventsListAdmin(namespace);
        }

        return eventsService.getNamespaceEventsList(namespace);

    }


    /**
     * Events 목록 조회(Get Events list)
     *
     * @param cluster     the cluster
     * @param namespace   the namespace
     * @param resourceUid the resourceUid
     * @param type        the type
     * @param isAdmin     the isAdmin
     * @return the events list
     */
    @ApiOperation(value = "Events 목록 조회(Get Events list)", nickname = "getEventsList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cluster", value = "클러스터 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "namespace", value = "네임스페이스 명", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "resourceUid", value = "리소스 uid", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "type", value = "타입", required = true, dataType = "string", paramType = "query")
    })
    @GetMapping(value = "/resources/{resourceUid:.+}")
    public Object getEventsList(@PathVariable(value = "cluster") String cluster,
                                @PathVariable(value = "namespace") String namespace,
                                @PathVariable(value = "resourceUid") String resourceUid,
                                @RequestParam(value = "type", required = false) String type,
                                @ApiIgnore @RequestParam(required = false, name = "isAdmin") boolean isAdmin) {

        if (isAdmin) {
            return eventsService.getEventsListAdmin(namespace, resourceUid, type);
        }

        return eventsService.getEventsList(namespace, resourceUid, type);
    }

}
