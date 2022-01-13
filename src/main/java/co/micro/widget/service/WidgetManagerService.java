package co.micro.widget.service;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;
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

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public Widget createWidget(CreateWidget request) {
        return setWriteLock(
            CreateWidget.builder()
                .widgetId(request.getWidgetId())
                .widgetName(request.getWidgetName())
                .coordinateX(request.getCoordinateX())
                .coordinateY(request.getCoordinateY())
                .coordinateZ(Objects.isNull(request.getCoordinateZ()) ? getMaxZ() : request.getCoordinateZ())
                .height(request.getHeight())
                .width(request.getWidth())
                .updatedAt(ZonedDateTime.now().toInstant().toEpochMilli())
                .createdAt(ZonedDateTime.now().toInstant().toEpochMilli())
                .build(),
            widget -> reorderAndCreateWidget(widget)
        );
    }

    @Override
    public Widget updateWidget(UpdateWidget request) {
        return setWriteLock(
            request,
            widget -> widgetRepository.getWidget(widget)
                .map(wg -> applyChanges(widget, wg))
                .map(wg -> reorderAndUpdateWidget(wg))
                .orElseThrow(() -> WidgetException.widgetNotFound(request.getWidgetId()))
        );
    }

    @Override
    public void deleteWidget(UUID widgetId) {
        lock.writeLock().lock();
        try {
            widgetRepository.deleteWidget(widgetId);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Widget getWidget(UUID widgetId) {
        lock.readLock().lock();
        try {
            return widgetRepository.getWidget(widgetId)
                .orElseThrow(() -> WidgetException.widgetNotFound(widgetId));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Widget> getWidgets(int limit, Long maxCoordinateX, Long maxCoordinateY, Long width, Long height) {
        lock.readLock().lock();
        try {
            return widgetRepository.getWidgets().stream()
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
                .limit(getLimit(limit))
                .collect(Collectors.toUnmodifiableList());
        } finally {
            lock.readLock().unlock();
        }
    }

    private Widget reorderAndCreateWidget(CreateWidget widget) {
        List<Widget> widgets = widgetRepository.getWidgets(widget.getCoordinateZ());

        reorderAndUpdateZ(widgets, widget.getCoordinateZ());

        return widgetRepository.createWidget(widget);
    }

    private Widget reorderAndUpdateWidget(UpdateWidget widget) {
        List<Widget> widgets = widgetRepository.getWidgets(widget.getCoordinateZ()).stream()
            .filter(wg -> !Objects.equals(wg.getWidgetId(), widget.getWidgetId()))
            .collect(Collectors.toList());

        reorderAndUpdateZ(widgets, widget.getCoordinateZ());

        return widgetRepository.updateWidget(widget);
    }

    private void reorderAndUpdateZ(List<Widget> widgets, Long newZ) {
        Widget updatedWidget = null;

        for (Widget widget : widgets) {
            Long curZ = widget.getCoordinateZ();

            if (Objects.isNull(updatedWidget) && Objects.equals(curZ, newZ)) {
                updatedWidget = incrementAndUpdateZ(widget, curZ);
            } else if (Objects.nonNull(updatedWidget) && Objects.equals(curZ, updatedWidget.getCoordinateZ())) {
                updatedWidget = incrementAndUpdateZ(widget, curZ);
            }
        }
    }

    private Widget incrementAndUpdateZ(Widget widget, Long z) {
        return widgetRepository.updateWidget(UpdateWidget.builder()
            .widgetId(widget.getWidgetId())
            .widgetName(widget.getWidgetName())
            .coordinateX(widget.getCoordinateX())
            .coordinateY(widget.getCoordinateY())
            .coordinateZ(increment(z))
            .height(widget.getHeight())
            .width(widget.getWidth())
            .updatedAt(widget.getUpdatedAt())
            .build());
    }

    private Long getMaxZ() {
        return widgetRepository.getWidgetMaxByCoordinateZ()
            .map(widget -> increment(widget.getCoordinateZ()))
            .orElse(Long.valueOf(0));
    }

    private <T, R> R setWriteLock(T widget, Function<T, R> manageWidgets) {
        lock.writeLock().lock();
        try {
            return manageWidgets.apply(widget);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private static Long increment(Long v) {
        return ++v;
    }

    private static int getLimit(int limit) {
        return limit == 0 ? ROW_LIMIT_DEFAULT : limit > ROW_LIMIT_MAX ? ROW_LIMIT_MAX : limit;
    }

    private static UpdateWidget applyChanges(UpdateWidget request, Widget widget) {
        UpdateWidget rr = UpdateWidget.builder()
            .widgetId(widget.getWidgetId())
            .widgetName(applyFirstNonNullable(request.getWidgetName(), widget.getWidgetName()))
            .coordinateX(applyFirstNonNullable(request.getCoordinateX(), widget.getCoordinateX()))
            .coordinateY(applyFirstNonNullable(request.getCoordinateY(), widget.getCoordinateY()))
            .coordinateZ(applyFirstNonNullable(request.getCoordinateZ(), widget.getCoordinateZ()))
            .width(applyFirstNonNullable(request.getWidth(), widget.getWidth()))
            .height(applyFirstNonNullable(request.getHeight(), widget.getHeight()))
            .updatedAt(ZonedDateTime.now().toInstant().toEpochMilli())
            .build();

        return rr;

//        return UpdateWidget.builder()
//            .widgetId(widget.getWidgetId())
//            .widgetName(applyFirstNonNullable(request.getWidgetName(), widget.getWidgetName()))
//            .coordinateX(applyFirstNonNullable(request.getCoordinateX(), widget.getCoordinateX()))
//            .coordinateY(applyFirstNonNullable(request.getCoordinateY(), widget.getCoordinateY()))
//            .coordinateZ(applyFirstNonNullable(request.getCoordinateZ(), widget.getCoordinateZ()))
//            .width(applyFirstNonNullable(request.getWidth(), widget.getWidth()))
//            .height(applyFirstNonNullable(request.getHeight(), widget.getHeight()))
//            .updatedAt(ZonedDateTime.now().toInstant().toEpochMilli())
//            .build();
    }

    private static <V> V applyFirstNonNullable(V v1, V v2) {
        return Optional.ofNullable(v1).orElse(v2);
    }
}
