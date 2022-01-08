package co.micro.widget.exception;

import java.util.UUID;


public class WidgetException extends RuntimeException {
    private WidgetException(String message) {
        super(message);
    }

    public static WidgetException widgetNotFound(UUID widgetId) {
        return new WidgetException(String.format("Widget not found! [id=%s]", widgetId));
    }
}
