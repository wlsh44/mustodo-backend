package mustodo.backend.dto.user;


import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Builder
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
    private boolean isTermsAndConditions;

}
