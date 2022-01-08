package co.micro.widget.entity;

import java.util.Objects;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;
import lombok.With;


@Data
@With
@Builder
public class Widget {

    private UUID widgetId;

    private String widgetName;

    private Long coordinateX;

    private Long coordinateY;

    private Long coordinateZ;

    private Long width;

    private Long height;

    private Long createdAt;

    private Long updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Widget widget = (Widget) o;

        if (!Objects.equals(widgetId, widget.widgetId)) return false;
        if (!Objects.equals(widgetName, widget.widgetName)) return false;
        if (!Objects.equals(coordinateX, widget.coordinateX)) return false;
        if (!Objects.equals(coordinateY, widget.coordinateY)) return false;
        if (!Objects.equals(coordinateZ, widget.coordinateZ)) return false;
        if (!Objects.equals(width, widget.width)) return false;
        if (!Objects.equals(height, widget.height)) return false;
        if (!Objects.equals(createdAt, widget.createdAt)) return false;
        return Objects.equals(updatedAt, widget.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(widgetId, widgetName, coordinateX, coordinateY, coordinateZ, width, height, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "Widget{" +
               "widgetId=" + widgetId +
               ", widgetName='" + widgetName + '\'' +
               ", coordinateX=" + coordinateX +
               ", coordinateY=" + coordinateY +
               ", coordinateZ=" + coordinateZ +
               ", width=" + width +
               ", height=" + height +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}
