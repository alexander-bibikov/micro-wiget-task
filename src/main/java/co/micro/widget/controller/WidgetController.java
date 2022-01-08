package co.micro.widget.controller;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import co.micro.widget.entity.CreateWidget;
import co.micro.widget.entity.UpdateWidget;
import co.micro.widget.entity.Widget;
import co.micro.widget.service.WidgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/widgets")
public class WidgetController {

    private static final String CONTENT_TYPE = "application/json";

    @Autowired
    private WidgetService widgetManager;

    @RequestMapping(method = RequestMethod.POST, consumes = CONTENT_TYPE)
    public ResponseEntity<Widget> createWidget(@RequestBody @Valid CreateWidget widget) {
        widget.setWidgetId(UUID.randomUUID());
        return response(widgetManager.createWidget(widget), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{widgetId}", method = RequestMethod.PATCH, consumes = CONTENT_TYPE)
    public ResponseEntity<Widget> updateWidget(@PathVariable UUID widgetId, @RequestBody @Valid UpdateWidget widget) {
        widget.setWidgetId(widgetId);
        return response(widgetManager.updateWidget(widget), HttpStatus.OK);
    }

    @RequestMapping(value = "/{widgetId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteWidget(@PathVariable UUID widgetId) {
        widgetManager.deleteWidget(widgetId);
    }

    @RequestMapping(value = "/{widgetId}", method = RequestMethod.GET, produces = CONTENT_TYPE)
    public ResponseEntity<Widget> getWidget(@PathVariable UUID widgetId) {
        return response(widgetManager.getWidget(widgetId), HttpStatus.OK);
    }

//    @RequestMapping(method = RequestMethod.GET, produces = CONTENT_TYPE)
//    public ResponseEntity<List<Widget>> getWidgets(@RequestParam(required = false) String limit) {
//        return response(
//            widgetManager.getWidgets(Optional.ofNullable(limit)
//                .map(Integer::valueOf)
//                .orElse(0)),
//            HttpStatus.OK);
//    }

    @RequestMapping(method = RequestMethod.GET, produces = CONTENT_TYPE)
    public ResponseEntity<List<Widget>> getFilteredWidgets(
        @RequestParam(required = false) String limit,
        @RequestParam(required = false) String maxCoordinateX,
        @RequestParam(required = false) String maxCoordinateY,
        @RequestParam(required = false) String width,
        @RequestParam(required = false) String height) {

        return response(widgetManager.getWidgets(
            Optional.ofNullable(limit).map(Integer::valueOf).orElse(0),
            getWhenDefined(maxCoordinateX),
            getWhenDefined(maxCoordinateY),
            getWhenDefined(width),
            getWhenDefined(height)
        ), HttpStatus.OK);
    }

    private static <T> ResponseEntity<T> response(T result, HttpStatus code) {
        return new ResponseEntity<>(result, code);
    }

    private static Long getWhenDefined(String v) {
        return Objects.nonNull(v) ? Long.valueOf(v) : null;
    }
}
