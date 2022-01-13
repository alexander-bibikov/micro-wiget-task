package co.micro.widget.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import co.micro.widget.entity.CreateWidget;
import co.micro.widget.entity.UpdateWidget;
import co.micro.widget.entity.Widget;
import co.micro.widget.exception.WidgetException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


@Repository
public class WidgetH2ManagerRepository implements WidgetRepository {

    @Autowired
    public JdbcTemplate jdbcTemplate;

    @Override
    public Widget createWidget(CreateWidget request) {
        jdbcTemplate.update(
            "INSERT INTO widgets (widget_id, widget_name, coordinate_x, coordinate_y, coordinate_z, width, height, updated_at, created_at)\n" +
            "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)",
            new Object[]{
                request.getWidgetId(),
                request.getWidgetName(),
                request.getCoordinateX(),
                request.getCoordinateY(),
                request.getCoordinateZ(),
                request.getWidth(),
                request.getHeight(),
                request.getUpdatedAt(),
                request.getCreatedAt()
            }
        );

        return getWidget(request.getWidgetId()).get();
    }

    @Override
    public Widget updateWidget(UpdateWidget request) {
        Pair<String, List<Object>> stmt = prepareUpdate(request);

        jdbcTemplate.update(stmt.getFirst(), stmt.getSecond().toArray());

        return getWidget(request.getWidgetId()).get();
    }

    @Override
    public void deleteWidget(UUID widgetId) {
        int deleted = jdbcTemplate.update("DELETE FROM widgets WHERE widget_id = ?",
            new Object[]{widgetId});

        if (deleted == 0) {
            throw WidgetException.widgetNotFound(widgetId);
        }
    }

    @Override
    public Optional<Widget> getWidget(UUID widgetId) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(
                "SELECT widget_id, widget_name, coordinate_x, coordinate_y, coordinate_z, width, height, updated_at, created_at FROM widgets\n" +
                "WHERE widget_id = ?",
                new WidgetMapper(),
                widgetId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Widget> getWidgetMaxByCoordinateZ() {
        try {
            return Optional.of(jdbcTemplate.queryForObject(
                "SELECT widget_id, widget_name, coordinate_x, coordinate_y, coordinate_z, width, height, updated_at, created_at FROM widgets\n" +
                "WHERE coordinate_z = (SELECT MAX(coordinate_z) FROM widgets)",
                new WidgetMapper()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Widget> getWidgets() {
        return jdbcTemplate.query(
            "SELECT widget_id, widget_name, coordinate_x, coordinate_y, coordinate_z, width, height, updated_at, created_at FROM widgets",
            new WidgetMapper());
    }

    @Override
    public List<Widget> getWidgets(Long startFromZ) {
        return jdbcTemplate.query(
            "WITH wg AS (\n" +
            "    SELECT *\n" +
            "    FROM widgets\n" +
            "    ORDER BY coordinate_z\n" +
            ")\n" +
            "SELECT * FROM wg\n" +
            "WHERE wg.coordinate_z >= ?",
            new WidgetMapper(),
            startFromZ);
    }

    private static Pair<String, List<Object>> prepareUpdate(UpdateWidget widget) {
        StringBuilder set = new StringBuilder("SET\n");
        List<Object> params = new LinkedList<>();

        if (Objects.nonNull(widget.getCoordinateX())) {
            set.append("coordinate_x = ?,\n");
            params.add(widget.getCoordinateX());
        }
        if (Objects.nonNull(widget.getCoordinateY())) {
            set.append("coordinate_y = ?,\n");
            params.add(widget.getCoordinateY());
        }
        if (Objects.nonNull(widget.getCoordinateZ())) {
            set.append("coordinate_z = ?,\n");
            params.add(widget.getCoordinateZ());
        }
        if (Objects.nonNull(widget.getWidth())) {
            set.append("width = ?,\n");
            params.add(widget.getWidth());
        }
        if (Objects.nonNull(widget.getHeight())) {
            set.append("height = ?,\n");
            params.add(widget.getHeight());
        }
        if (Objects.nonNull(widget.getWidgetName())) {
            set.append("widget_name = ?,\n");
            params.add(widget.getWidgetName());
        }

        set.append("updated_at = ?,\n");
        params.add(widget.getUpdatedAt());

        params.add(widget.getWidgetId());

        return Pair.of(
            new StringBuilder("UPDATE widgets\n")
                .append(set.toString().replaceAll(",$", ""))
                .append("WHERE widget_id = ?")
                .toString(),
            params);
    }

    private static final class WidgetMapper implements RowMapper<Widget> {
        public Widget mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Widget.builder()
                .widgetId(UUID.fromString(rs.getString("widget_id")))
                .widgetName(rs.getString("widget_name"))
                .coordinateX(rs.getLong("coordinate_x"))
                .coordinateY(rs.getLong("coordinate_y"))
                .coordinateZ(rs.getLong("coordinate_z"))
                .width(rs.getLong("width"))
                .height(rs.getLong("height"))
                .updatedAt(rs.getLong("updated_at"))
                .createdAt(rs.getLong("created_at"))
                .build();
        }
    }
}
