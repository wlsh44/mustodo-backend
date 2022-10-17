package mustodo.backend.exception.auth;

import mustodo.backend.exception.enums.ErrorCode;
import org.springframework.http.HttpStatus;

import static mustodo.backend.exception.enums.AuthErrorCode.UNAUTHORIZED_ACCESS;

public class NotAuthorizedException extends AuthException {

    private static final ErrorCode errorCode = UNAUTHORIZED_ACCESS;

    public NotAuthorizedException() {
        super(errorCode.getErrMsg(), HttpStatus.UNAUTHORIZED, errorCode);
    }
}
