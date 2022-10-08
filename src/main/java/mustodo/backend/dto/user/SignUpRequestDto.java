package mustodo.backend.dto.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDto {

    @Email
    @NotNull
    private String email;

    @NotNull
    private String name;

    @NotNull
    private String password;

    @NotNull
    private String passwordConfirm;

    @NotNull
    private boolean termsAndConditions;

}
