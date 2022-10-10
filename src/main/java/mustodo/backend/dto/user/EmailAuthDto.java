package mustodo.backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder
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
