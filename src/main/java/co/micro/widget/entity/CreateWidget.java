package co.micro.widget.entity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.With;


@Data
@With
@Builder
public class CreateWidget {

    @JsonIgnore
    private UUID widgetId;

    @NotNull
    private String widgetName;

    @NotNull
    private Long coordinateX;

    @NotNull
    private Long coordinateY;

    private Long coordinateZ;

    private Long createdAt;

    private Long updatedAt;

    @NotNull
    @Positive
    private Long width;

    @NotNull
    @Positive
    private Long height;
}
