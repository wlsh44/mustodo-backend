package mustodo.backend.user.ui.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateUserDto {
    private String name;
    private String biography;
}
