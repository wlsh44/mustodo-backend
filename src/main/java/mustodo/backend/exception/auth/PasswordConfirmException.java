package mustodo.backend.exception.auth;

import mustodo.backend.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

import static mustodo.backend.exception.errorcode.AuthErrorCode.PASSWORD_CONFIRM_FAILED;

public class PasswordConfirmException extends AuthException {

    private static final ErrorCode errorCode = PASSWORD_CONFIRM_FAILED;

    public PasswordConfirmException() {
        super(errorCode.getErrMsg(), HttpStatus.BAD_REQUEST, errorCode);
    }
}
