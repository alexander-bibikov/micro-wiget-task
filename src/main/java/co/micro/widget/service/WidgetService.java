package co.micro.widget.service;

import java.util.List;
import java.util.UUID;

import co.micro.widget.entity.CreateWidget;
import co.micro.widget.entity.UpdateWidget;
import co.micro.widget.entity.Widget;


public interface WidgetService {

    Widget createWidget(CreateWidget request);

    Widget updateWidget(UpdateWidget request);

    void deleteWidget(UUID widgetId);

    Widget getWidget(UUID widgetId);

    List<Widget> getWidgets(
        int page,
        int limit,
        Long maxCoordinateX,
        Long maxCoordinateY,
        Long width,
        Long height
    );
}
