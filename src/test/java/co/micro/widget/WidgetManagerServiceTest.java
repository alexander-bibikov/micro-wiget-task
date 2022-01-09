package co.micro.widget;

import java.util.List;
import java.util.UUID;

import co.micro.widget.entity.CreateWidget;
import co.micro.widget.entity.UpdateWidget;
import co.micro.widget.entity.Widget;
import co.micro.widget.service.WidgetManagerService;
import co.micro.widget.service.WidgetService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static co.micro.widget.helpers.WidgetHelper.checkWidget;
import static co.micro.widget.helpers.WidgetHelper.getCreateRequest;
import static co.micro.widget.helpers.WidgetHelper.getUpdateRequest;
import static co.micro.widget.helpers.WidgetHelper.getWidget;
import static co.micro.widget.helpers.WidgetHelper.toLong;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class WidgetManagerServiceTest {

    private static final UUID WIDGET_ID = UUID.randomUUID();

    @Autowired
    private WidgetService widgetManager;

    @Test
    public void updateWidget() {
        createWidget(getCreateRequest("Widget_1", 9, 8, 1, 10, 20, WIDGET_ID));

        updateWidget(getUpdateRequest("Widget_2", 10, 4, 2, 20, 30, WIDGET_ID));
        List<Widget> actualWidgets = getWidgets();

        assertFalse(actualWidgets.isEmpty());
        checkWidget(actualWidgets.get(0), getWidget("Widget_2", 10, 4, 2, 20, 30));
    }

    @Test
    public void deleteWidget() {
        createWidget(getCreateRequest("Widget_1", 9, 8, 1, 10, 20, WIDGET_ID));
        deleteWidget(WIDGET_ID);

        List<Widget> actualWidgets = getWidgets();

        assertTrue(actualWidgets.isEmpty());
    }

    @Test
    public void createWidgetWithNullableZAndNoRows() {
        CreateWidget request = getCreateRequest("Widget_1", toLong(9), toLong(8), null, toLong(10), toLong(20), WIDGET_ID);
        createWidget(request);

        List<Widget> actualWidgets = getWidgets();
        Widget actualWidget = Widget.builder()
            .widgetId(request.getWidgetId())
            .widgetName(request.getWidgetName())
            .coordinateX(request.getCoordinateX())
            .coordinateY(request.getCoordinateY())
            .coordinateZ(toLong(0))
            .height(request.getHeight())
            .width(request.getWidth())
            .build();

        assertFalse(actualWidgets.isEmpty());
        checkWidget(actualWidgets.get(0), actualWidget);
    }

    @Test
    public void createWidgetWithNullableZAndExistedRows() {
        CreateWidget request = getCreateRequest("Widget_4", toLong(9), toLong(8), null, toLong(10), toLong(20), WIDGET_ID);
        List<CreateWidget> requests = List.of(
            getCreateRequest("Widget_2", 3, 4, 5, 33, 56, UUID.randomUUID()),
            getCreateRequest("Widget_1", 7, 1, 1, 16, 26, UUID.randomUUID()),
            getCreateRequest("Widget_3", 5, 7, 3, 78, 31, UUID.randomUUID())
        );

        createWidgets(requests);
        createWidget(request);

        List<Widget> actualWidgets = getWidgets();

        assertFalse(actualWidgets.isEmpty());
        assertEquals(actualWidgets.size(), 4);
        checkWidget(
            actualWidgets.get(0),
            getWidget("Widget_1", 7, 1, 1, 16, 26)
        );
        checkWidget(
            actualWidgets.get(1),
            getWidget("Widget_3", 5, 7, 3, 78, 31)
        );
        checkWidget(
            actualWidgets.get(2),
            getWidget("Widget_2", 3, 4, 5, 33, 56)
        );
        checkWidget(
            actualWidgets.get(3),
            getWidget("Widget_4", 9, 8, 6, 10, 20)
        );
    }

    @Test
    public void addWidgetWith2And3Shift() {
        List<CreateWidget> requests = List.of(
            getCreateRequest("Widget_2", 3, 4, 2, 33, 56, UUID.randomUUID()),
            getCreateRequest("Widget_1", 7, 1, 1, 16, 26, UUID.randomUUID()),
            getCreateRequest("Widget_3", 5, 7, 3, 78, 31, UUID.randomUUID())
        );

        createWidgets(requests);
        createWidget(getCreateRequest("Widget_4", 9, 8, 2, 10, 20, WIDGET_ID));

        List<Widget> actualWidgets = getWidgets();

        assertFalse(actualWidgets.isEmpty());
        assertEquals(actualWidgets.size(), 4);
        checkWidget(
            actualWidgets.get(0),
            getWidget("Widget_1", 7, 1, 1, 16, 26)
        );
        checkWidget(
            actualWidgets.get(1),
            getWidget("Widget_4", 9, 8, 2, 10, 20)
        );
        checkWidget(
            actualWidgets.get(2),
            getWidget("Widget_2", 3, 4, 3, 33, 56)
        );
        checkWidget(
            actualWidgets.get(3),
            getWidget("Widget_3", 5, 7, 4, 78, 31)
        );
    }

    @Test
    public void addWidgetNoShift() {
        List<CreateWidget> requests = List.of(
            getCreateRequest("Widget_2", 3, 4, 5, 33, 56, UUID.randomUUID()),
            getCreateRequest("Widget_1", 7, 1, 1, 16, 26, UUID.randomUUID()),
            getCreateRequest("Widget_3", 5, 7, 6, 78, 31, UUID.randomUUID())
        );

        createWidgets(requests);
        createWidget(getCreateRequest("Widget_4", 9, 8, 2, 10, 20, WIDGET_ID));

        List<Widget> actualWidgets = getWidgets();

        assertFalse(actualWidgets.isEmpty());
        assertEquals(actualWidgets.size(), 4);
        checkWidget(
            actualWidgets.get(0),
            getWidget("Widget_1", 7, 1, 1, 16, 26)
        );
        checkWidget(
            actualWidgets.get(1),
            getWidget("Widget_4", 9, 8, 2, 10, 20)
        );
        checkWidget(
            actualWidgets.get(2),
            getWidget("Widget_2", 3, 4, 5, 33, 56)
        );
        checkWidget(
            actualWidgets.get(3),
            getWidget("Widget_3", 5, 7, 6, 78, 31)
        );
    }

    @Test
    public void addWidgetWith2Shift() {
        List<CreateWidget> requests = List.of(
            getCreateRequest("Widget_2", 3, 4, 2, 33, 56, UUID.randomUUID()),
            getCreateRequest("Widget_1", 7, 1, 1, 16, 26, UUID.randomUUID()),
            getCreateRequest("Widget_3", 5, 7, 4, 78, 31, UUID.randomUUID())
        );

        createWidgets(requests);
        createWidget(getCreateRequest("Widget_4", 9, 8, 2, 10, 20, WIDGET_ID));

        List<Widget> actualWidgets = getWidgets();

        assertFalse(actualWidgets.isEmpty());
        assertEquals(actualWidgets.size(), 4);
        checkWidget(
            actualWidgets.get(0),
            getWidget("Widget_1", 7, 1, 1, 16, 26)
        );
        checkWidget(
            actualWidgets.get(1),
            getWidget("Widget_4", 9, 8, 2, 10, 20)
        );
        checkWidget(
            actualWidgets.get(2),
            getWidget("Widget_2", 3, 4, 3, 33, 56)
        );
        checkWidget(
            actualWidgets.get(3),
            getWidget("Widget_3", 5, 7, 4, 78, 31)
        );
    }

    @Test
    public void updateWidgetWith3Shift() {
        List<CreateWidget> requests = List.of(
            getCreateRequest("Widget_4", 9, 8, 4, 10, 20, WIDGET_ID),
            getCreateRequest("Widget_2", 3, 4, 2, 33, 56, UUID.randomUUID()),
            getCreateRequest("Widget_1", 7, 1, 1, 16, 26, UUID.randomUUID()),
            getCreateRequest("Widget_3", 5, 7, 3, 78, 31, UUID.randomUUID())
        );

        createWidgets(requests);
        updateWidget(setCoordinateZ(1));

        List<Widget> actualWidgets = getWidgets();

        assertFalse(actualWidgets.isEmpty());
        assertEquals(actualWidgets.size(), 4);
        checkWidget(
            actualWidgets.get(0),
            getWidget("Widget_4", 9, 8, 1, 10, 20)
        );
        checkWidget(
            actualWidgets.get(1),
            getWidget("Widget_1", 7, 1, 2, 16, 26)
        );
        checkWidget(
            actualWidgets.get(2),
            getWidget("Widget_2", 3, 4, 3, 33, 56)
        );
        checkWidget(
            actualWidgets.get(3),
            getWidget("Widget_3", 5, 7, 4, 78, 31)
        );
    }

    @Test
    public void updateWidgetWith1Shift() {
        List<CreateWidget> requests = List.of(
            getCreateRequest("Widget_4", 9, 8, 7, 10, 20, WIDGET_ID),
            getCreateRequest("Widget_2", 3, 4, 8, 33, 56, UUID.randomUUID()),
            getCreateRequest("Widget_5", 9, 6, 6, 31, 16, UUID.randomUUID()),
            getCreateRequest("Widget_1", 7, 1, 1, 16, 26, UUID.randomUUID()),
            getCreateRequest("Widget_3", 5, 7, 2, 78, 31, UUID.randomUUID())
        );

        createWidgets(requests);
        updateWidget(setCoordinateZ(6));

        List<Widget> actualWidgets = getWidgets();

        assertFalse(actualWidgets.isEmpty());
        assertEquals(actualWidgets.size(), 5);
        checkWidget(
            actualWidgets.get(0),
            getWidget("Widget_1", 7, 1, 1, 16, 26)
        );
        checkWidget(
            actualWidgets.get(1),
            getWidget("Widget_3", 5, 7, 2, 78, 31)
        );
        checkWidget(
            actualWidgets.get(2),
            getWidget("Widget_4", 9, 8, 6, 10, 20)
        );
        checkWidget(
            actualWidgets.get(3),
            getWidget("Widget_5", 9, 6, 7, 31, 16)
        );
        checkWidget(
            actualWidgets.get(4),
            getWidget("Widget_2", 3, 4, 8, 33, 56)
        );
    }

    @Test
    public void updateWidgetWithNoShift() {
        List<CreateWidget> requests = List.of(
            getCreateRequest("Widget_4", 9, 8, 8, 10, 20, WIDGET_ID),
            getCreateRequest("Widget_2", 3, 4, 7, 33, 56, UUID.randomUUID()),
            getCreateRequest("Widget_1", 7, 1, 1, 16, 26, UUID.randomUUID()),
            getCreateRequest("Widget_3", 5, 7, 2, 78, 31, UUID.randomUUID())
        );

        createWidgets(requests);
        updateWidget(setCoordinateZ(0));

        List<Widget> actualWidgets = getWidgets();

        assertFalse(actualWidgets.isEmpty());
        assertEquals(actualWidgets.size(), 4);
        checkWidget(
            actualWidgets.get(0),
            getWidget("Widget_4", 9, 8, 0, 10, 20)
        );
        checkWidget(
            actualWidgets.get(1),
            getWidget("Widget_1", 7, 1, 1, 16, 26)
        );
        checkWidget(
            actualWidgets.get(2),
            getWidget("Widget_3", 5, 7, 2, 78, 31)
        );
        checkWidget(
            actualWidgets.get(3),
            getWidget("Widget_2", 3, 4, 7, 33, 56)
        );
    }

    @Test
    public void searchWidgets() {
        long maxCoordinateX = 100L;
        long maxCoordinateY = 150L;
        long width = 100L;
        long height = 100L;

        List<CreateWidget> requests = List.of(
            getCreateRequest("Widget_1", 50, 50, 1, 100, 100, UUID.randomUUID()),
            getCreateRequest("Widget_2", 50, 150, 2, 100, 100, UUID.randomUUID()),
            getCreateRequest("Widget_3", 100, 100, 3, 100, 100, UUID.randomUUID()),
            getCreateRequest("Widget_4", 50, 100, 4, 100, 100, UUID.randomUUID()),
            getCreateRequest("Widget_5", 100, 150, 5, 100, 100, UUID.randomUUID()),
            getCreateRequest("Widget_6", 150, 150, 6, 100, 100, UUID.randomUUID())
        );

        createWidgets(requests);

        List<Widget> actualWidgets = getWidgets(maxCoordinateX, maxCoordinateY, width, height);

        assertFalse(actualWidgets.isEmpty());
        assertEquals(actualWidgets.size(), 2);
        checkWidget(
            actualWidgets.get(0),
            getWidget("Widget_1", 50, 50, 1, 100, 100)
        );
        checkWidget(
            actualWidgets.get(1),
            getWidget("Widget_4", 50, 100, 4, 100, 100)
        );
    }

    private Widget createWidget(CreateWidget widget) {
        return widgetManager.createWidget(widget);
    }

    private Widget updateWidget(UpdateWidget widget) {
        return widgetManager.updateWidget(widget);
    }

    private void deleteWidget(UUID widgetId) {
        widgetManager.deleteWidget(widgetId);
    }

    private List<Widget> getWidgets() {
        return widgetManager.getWidgets(
            WidgetManagerService.ROW_LIMIT_DEFAULT, null, null, null, null);
    }

    private List<Widget> getWidgets(long maxCoordinateX, long maxCoordinateY, long width, long height) {
        return widgetManager.getWidgets(
            WidgetManagerService.ROW_LIMIT_DEFAULT,
            toLong(maxCoordinateX),
            toLong(maxCoordinateY),
            toLong(width),
            toLong(height));
    }

    private void createWidgets(List<CreateWidget> requests) {
        requests.forEach(request -> createWidget(request));
    }

    private static UpdateWidget setCoordinateZ(long z) {
        return UpdateWidget.builder()
            .widgetId(WIDGET_ID)
            .coordinateZ(z)
            .build();
    }
}
