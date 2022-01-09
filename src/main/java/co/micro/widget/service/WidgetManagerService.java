package co.micro.widget.service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import co.micro.widget.entity.CreateWidget;
import co.micro.widget.entity.UpdateWidget;
import co.micro.widget.entity.Widget;
import co.micro.widget.exception.WidgetException;
import co.micro.widget.repository.WidgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class WidgetManagerService implements WidgetService {

    public static final int ROW_LIMIT_MAX = 500;
    public static final int ROW_LIMIT_DEFAULT = 10;

    @Autowired
    private WidgetRepository widgetRepository;

    @Override
    public synchronized Widget createWidget(CreateWidget request) {
        CreateWidget widget = CreateWidget.builder()
            .widgetId(request.getWidgetId())
            .widgetName(request.getWidgetName())
            .coordinateX(request.getCoordinateX())
            .coordinateY(request.getCoordinateY())
            .coordinateZ(Objects.isNull(request.getCoordinateZ()) ? getMaxZ() : request.getCoordinateZ())
            .height(request.getHeight())
            .width(request.getWidth())
            .build();

        return reorderAndCreateWidget(widget);
    }

    @Override
    public synchronized Widget updateWidget(UpdateWidget request) {
        return widgetRepository.getWidget(request.getWidgetId())
            .map(widget -> applyChanges(request, widget))
            .map(widget -> reorderAndUpdateWidget(widget))
            .orElseThrow(() -> WidgetException.widgetNotFound(request.getWidgetId()));
    }

    @Override
    public synchronized void deleteWidget(UUID widgetId) {
        widgetRepository.deleteWidget(widgetId);
    }

    @Override
    public Widget getWidget(UUID widgetId) {
        return widgetRepository.getWidget(widgetId)
            .orElseThrow(() -> WidgetException.widgetNotFound(widgetId));
    }

    @Override
    public List<Widget> getWidgets(int limit, Long maxCoordinateX, Long maxCoordinateY, Long width, Long height) {
        return widgetRepository.getWidgets()
            .stream()
            .limit(getLimit(limit))
            //TODO: This is a draft filtering
            .filter(wg -> Objects.nonNull(maxCoordinateY) ? wg.getCoordinateY() >= 0 && wg.getCoordinateY() <= maxCoordinateY : true)
            .filter(wg -> Objects.nonNull(maxCoordinateX) ? wg.getCoordinateX() >= 0 && wg.getCoordinateX() <= maxCoordinateX : true)
            .filter(wg -> Objects.nonNull(width) ? wg.getWidth() == width : true)
            .filter(wg -> Objects.nonNull(height) ? wg.getHeight() == height : true)
            .filter(wg -> Objects.nonNull(maxCoordinateY) && Objects.nonNull(height) ?
                wg.getCoordinateY() + Double.valueOf(wg.getHeight()) / 2 <= maxCoordinateY : true)
            .filter(wg -> Objects.nonNull(maxCoordinateX) && Objects.nonNull(width) ?
                wg.getCoordinateX() + Double.valueOf(wg.getWidth()) / 2 <= maxCoordinateX : true)
            .sorted(Comparator.comparingLong(Widget::getCoordinateZ))
            .collect(Collectors.toUnmodifiableList());
    }

    private Widget reorderAndCreateWidget(CreateWidget widget) {
        SortedMap<Long, Widget> zLastTail = getSortedTailByZ(widget.getCoordinateZ());
        reorderAndUpdateZ(zLastTail, widget.getCoordinateZ());

        return widgetRepository.createWidget(widget);
    }

    private Widget reorderAndUpdateWidget(UpdateWidget widget) {
        SortedMap<Long, Widget> zLastTail = getSortedTailByZ(widget.getCoordinateZ(), widget.getWidgetId());
        reorderAndUpdateZ(zLastTail, widget.getCoordinateZ());

        return widgetRepository.updateWidget(widget);
    }

    private void reorderAndUpdateZ(SortedMap<Long, Widget> zLastTail, Long newZ) {
        Widget updatedWidget = null;

        for (Widget widget : zLastTail.values()) {
            Long curZ = widget.getCoordinateZ();

            if (Objects.isNull(updatedWidget) && Objects.equals(curZ, newZ)) {
                updatedWidget = incrementAndUpdateZ(widget.getWidgetId(), curZ);
            } else if (Objects.nonNull(updatedWidget) && Objects.equals(curZ, updatedWidget.getCoordinateZ())) {
                updatedWidget = incrementAndUpdateZ(widget.getWidgetId(), curZ);
            }
        }
    }

    private Widget incrementAndUpdateZ(UUID widgetId, Long z) {
        return widgetRepository.updateWidget(UpdateWidget.builder()
            .widgetId(widgetId)
            .coordinateZ(increment(z))
            .build());
    }

    private Long getMaxZ() {
        return widgetRepository.getWidgets()
            .stream()
            .max(Comparator.comparingLong(Widget::getCoordinateZ))
            .map(widget -> increment(widget.getCoordinateZ()))
            .orElse(Long.valueOf(0));
    }

    private SortedMap<Long, Widget> getSortedTailByZ(Long z, UUID excludeWidgetId) {
        List<Widget> widgets = widgetRepository.getWidgets()
            .stream()
            .filter(widget -> !Objects.equals(widget.getWidgetId(), excludeWidgetId))
            .collect(Collectors.toList());

        return sortByZ(widgets).tailMap(z);
    }

    private SortedMap<Long, Widget> getSortedTailByZ(Long z) {
        List<Widget> widgets = widgetRepository.getWidgets();

        return sortByZ(widgets).tailMap(z);
    }

    private static Long increment(Long v) {
        return ++v;
    }

    private static TreeMap<Long, Widget> sortByZ(List<Widget> widgets) {
        return widgets.stream()
            .collect(Collectors.toMap(
                Widget::getCoordinateZ,
                Function.identity(),
                (a, b) -> b,
                TreeMap::new
            ));
    }

    private static int getLimit(int limit) {
        return limit == 0 ? ROW_LIMIT_DEFAULT : limit > ROW_LIMIT_MAX ? ROW_LIMIT_MAX : limit;
    }

    private static UpdateWidget applyChanges(UpdateWidget request, Widget widget) {
        return UpdateWidget.builder()
            .widgetId(widget.getWidgetId())
            .widgetName(applyFirstNonNullable(request.getWidgetName(), widget.getWidgetName()))
            .coordinateX(applyFirstNonNullable(request.getCoordinateX(), widget.getCoordinateX()))
            .coordinateY(applyFirstNonNullable(request.getCoordinateY(), widget.getCoordinateY()))
            .coordinateZ(applyFirstNonNullable(request.getCoordinateZ(), widget.getCoordinateZ()))
            .width(applyFirstNonNullable(request.getWidth(), widget.getWidth()))
            .height(applyFirstNonNullable(request.getHeight(), widget.getHeight()))
            .build();
    }

    private static <V> V applyFirstNonNullable(V v1, V v2) {
        return Optional.ofNullable(v1).orElse(v2);
    }
}
