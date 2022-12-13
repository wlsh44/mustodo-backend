package mustodo.backend.todo.ui.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mustodo.backend.todo.ui.dto.validator.HexColor;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCategoryDto {

    @NotNull
    private String name;

    @NotNull
    @HexColor
    private String color;

    @NotNull
    @JsonProperty(value = "isPublic")
    private boolean publicAccess;
}
