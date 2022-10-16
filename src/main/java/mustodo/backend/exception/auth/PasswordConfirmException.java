package mustodo.backend.exception.auth;

import mustodo.backend.enums.error.ErrorCode;
import org.springframework.http.HttpStatus;

import static mustodo.backend.enums.auth.SignUpErrorCode.PASSWORD_CONFIRM_FAILED;

public class PasswordConfirmException extends AuthException {

    private static final ErrorCode errorCode = PASSWORD_CONFIRM_FAILED;

    public PasswordConfirmException() {
        super(errorCode.getErrMsg(), HttpStatus.BAD_REQUEST, errorCode);
    }
}
