package mustodo.backend.exception.auth;

import mustodo.backend.exception.enums.ErrorCode;
import org.springframework.http.HttpStatus;

import static mustodo.backend.exception.enums.AuthErrorCode.INVALID_EMAIL_AUTH_KEY;

public class InvalidEmailAuthKeyException extends AuthException {

    private static final ErrorCode errorCode = INVALID_EMAIL_AUTH_KEY;

    public InvalidEmailAuthKeyException(String name) {
        super(String.format(errorCode.getErrMsg(), name), HttpStatus.BAD_REQUEST, errorCode);
    }
}
