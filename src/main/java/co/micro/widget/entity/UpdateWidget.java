package co.micro.widget.entity;

import javax.validation.constraints.Positive;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.With;


@Data
@With
@Builder
public class UpdateWidget {

    @JsonIgnore
    private UUID widgetId;

    private String widgetName;

    private Long coordinateX;

    private Long coordinateY;

    private Long coordinateZ;

    @Positive
    private Long width;

    @Positive
    private Long height;
}
