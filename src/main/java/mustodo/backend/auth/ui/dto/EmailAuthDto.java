package mustodo.backend.auth.ui.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class EmailAuthDto {

    @NotNull
    @Length(min = 6, max = 6)
    private String authKey;

    @NotNull
    @Email
    private String email;
}
