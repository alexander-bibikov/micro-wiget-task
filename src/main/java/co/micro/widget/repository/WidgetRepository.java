package co.micro.widget.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import co.micro.widget.entity.CreateWidget;
import co.micro.widget.entity.UpdateWidget;
import co.micro.widget.entity.Widget;


public interface WidgetRepository {

    Widget createWidget(CreateWidget request);

    Widget updateWidget(UpdateWidget request);

    void deleteWidget(UUID widgetId);

    Optional<Widget> getWidget(UUID widgetId);

    List<Widget> getWidgets();
}
