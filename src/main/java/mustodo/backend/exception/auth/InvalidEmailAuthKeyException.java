package mustodo.backend.exception.auth;

import mustodo.backend.enums.error.ErrorCode;
import org.springframework.http.HttpStatus;

import static mustodo.backend.enums.auth.SignUpErrorCode.ALREADY_EXISTS_NAME;

public class InvalidEmailAuthKeyException extends AuthException {

    private static final ErrorCode errorCode = ALREADY_EXISTS_NAME;

    public InvalidEmailAuthKeyException(String name) {
        super(String.format(errorCode.getErrMsg(), name), HttpStatus.BAD_REQUEST, errorCode);
    }
}
