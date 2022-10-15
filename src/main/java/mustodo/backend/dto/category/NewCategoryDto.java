package mustodo.backend.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class NewCategoryDto {

    @NotNull
    private String name;

    @NotNull
    @Length(min = 6, max = 6)
    private String color;
}
