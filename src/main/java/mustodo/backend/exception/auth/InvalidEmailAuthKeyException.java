package mustodo.backend.exception.auth;

import mustodo.backend.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

import static mustodo.backend.exception.errorcode.AuthErrorCode.INVALID_EMAIL_AUTH_KEY;

public class InvalidEmailAuthKeyException extends AuthException {

    private static final ErrorCode errorCode = INVALID_EMAIL_AUTH_KEY;

    public InvalidEmailAuthKeyException(String name) {
        super(String.format(errorCode.getErrMsg(), name), HttpStatus.BAD_REQUEST, errorCode);
    }
}
