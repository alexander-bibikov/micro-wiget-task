package co.micro.widget.helpers;

import java.util.UUID;

import co.micro.widget.entity.CreateWidget;
import co.micro.widget.entity.UpdateWidget;
import co.micro.widget.entity.Widget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class WidgetHelper {

    public static CreateWidget getCreateRequest(String name, long x, long y, long z, long height, long width) {
        return getCreateRequest(name, toLong(x), toLong(y), toLong(z), toLong(height), toLong(width), null);
    }

    public static CreateWidget getCreateRequest(String name, long x, long y, long z, long height, long width, UUID widgetId) {
        return getCreateRequest(name, toLong(x), toLong(y), toLong(z), toLong(height), toLong(width), widgetId);
    }

    public static UpdateWidget getUpdateRequest(String name, long x, long y, long z, long height, long width, UUID widgetId) {
        return getUpdateRequest(name, toLong(x), toLong(y), toLong(z), toLong(height), toLong(width), widgetId);
    }

    public static CreateWidget getCreateRequest(String name, Long x, Long y, Long z, Long height, Long width, UUID widgetId) {
        return CreateWidget.builder()
            .widgetId(widgetId)
            .widgetName(name)
            .coordinateX(x)
            .coordinateY(y)
            .coordinateZ(z)
            .height(height)
            .width(width)
            .build();
    }

    public static UpdateWidget getUpdateRequest(String name, Long x, Long y, Long z, Long height, Long width, UUID widgetId) {
        return UpdateWidget.builder()
            .widgetId(widgetId)
            .widgetName(name)
            .coordinateX(x)
            .coordinateY(y)
            .coordinateZ(z)
            .height(height)
            .width(width)
            .build();
    }

    public static Widget getWidget(String name, long x, long y, long z, long height, long width) {
        return Widget.builder()
            .widgetName(name)
            .coordinateX(toLong(x))
            .coordinateY(toLong(y))
            .coordinateZ(toLong(z))
            .height(toLong(height))
            .width(toLong(width))
            .build();
    }

    public static void checkWidget(Widget actual, Widget expected) {
        assertNotNull(actual);

        assertEquals(actual.getCoordinateX(), expected.getCoordinateX());
        assertEquals(actual.getCoordinateY(), expected.getCoordinateY());
        assertEquals(actual.getCoordinateZ(), expected.getCoordinateZ());
        assertEquals(actual.getWidth(), expected.getWidth());
        assertEquals(actual.getHeight(), expected.getHeight());
    }

    public static Long toLong(long v) {
        return Long.valueOf(v);
    }
}
