package mustodo.backend.dto.user;

import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Builder
public class EmailAuthDto {

    @NotNull
    @Length(min = 6, max = 6)
    private String authKey;

    @NotNull
    @Email
    private String email;
}
