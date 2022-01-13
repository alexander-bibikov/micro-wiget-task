package co.micro.widget.repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import co.micro.widget.entity.CreateWidget;
import co.micro.widget.entity.UpdateWidget;
import co.micro.widget.entity.Widget;


public interface WidgetRepository {

    Widget createWidget(CreateWidget request);

    Widget updateWidget(UpdateWidget request);

    void deleteWidget(UUID widgetId);

    Optional<Widget> getWidget(UUID widgetId);

    List<Widget> getWidgets();

    default Optional<Widget> getWidget(UpdateWidget request) {
        return getWidget(request.getWidgetId());
    }

    default List<Widget> getWidgets(Long startFromZ) {
        return getWidgets().stream()
            .filter(widget -> widget.getCoordinateZ() >= startFromZ)
            .sorted(Comparator.comparingLong(Widget::getCoordinateZ))
            .collect(Collectors.toList());
    }

    default Optional<Widget> getWidgetMaxByCoordinateZ() {
        List<Widget> widgets = getWidgets();

        return !widgets.isEmpty() ?
            widgets.stream()
                .max(Comparator.comparingLong(Widget::getCoordinateZ)) :
            Optional.empty();
    }
}
