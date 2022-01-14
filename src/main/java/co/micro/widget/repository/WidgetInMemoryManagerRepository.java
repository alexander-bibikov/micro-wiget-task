package co.micro.widget.repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import co.micro.widget.entity.CreateWidget;
import co.micro.widget.entity.UpdateWidget;
import co.micro.widget.entity.Widget;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;


@Repository
@Primary
public class WidgetInMemoryManagerRepository implements WidgetRepository {

    private Map<UUID, Widget> widgets = new TreeMap<>();

    @Override
    public Widget createWidget(CreateWidget request) {
        return put(convertToWidget(request));
    }

    @Override
    public Widget updateWidget(UpdateWidget request) {
        return put(convertToWidget(request));
    }

    @Override
    public void deleteWidget(UUID widgetId) {
        widgets.remove(widgetId);
    }

    @Override
    public Optional<Widget> getWidget(UUID widgetId) {
        Widget wg = widgets.get(widgetId);

        return Objects.nonNull(wg) ?
            Optional.of(wg) :
            Optional.empty();
    }

    @Override
    public List<Widget> getWidgets(Long startFromZ) {
        return widgets.values().stream()
            .filter(widget -> widget.getCoordinateZ() >= startFromZ)
            .sorted(Comparator.comparingLong(Widget::getCoordinateZ))
            .collect(Collectors.toList());
    }

    @Override
    public List<Widget> getWidgets() {
        return new ArrayList(widgets.values());
    }

    private Widget put(Widget widget) {
        widgets.put(widget.getWidgetId(), widget);

        return widgets.get(widget.getWidgetId());
    }

    @Override
    public Optional<Widget> getWidgetMaxByCoordinateZ() {
        return !widgets.isEmpty() ?
            widgets.values().stream()
                .max(Comparator.comparingLong(Widget::getCoordinateZ)) :
            Optional.empty();
    }

    private static Widget convertToWidget(CreateWidget request) {
        return Widget.builder()
            .widgetId(request.getWidgetId())
            .widgetName(request.getWidgetName())
            .coordinateX(request.getCoordinateX())
            .coordinateY(request.getCoordinateY())
            .coordinateZ(request.getCoordinateZ())
            .height(request.getHeight())
            .width(request.getWidth())
            .updatedAt(request.getUpdatedAt())
            .createdAt(request.getCreatedAt())
            .build();
    }

    private static Widget convertToWidget(UpdateWidget request) {
        return Widget.builder()
            .widgetId(request.getWidgetId())
            .widgetName(request.getWidgetName())
            .coordinateX(request.getCoordinateX())
            .coordinateY(request.getCoordinateY())
            .coordinateZ(request.getCoordinateZ())
            .updatedAt(request.getUpdatedAt())
            .height(request.getHeight())
            .width(request.getWidth())
            .build();
    }
}
