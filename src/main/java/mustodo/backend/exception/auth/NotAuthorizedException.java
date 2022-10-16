package mustodo.backend.exception.auth;

import mustodo.backend.enums.error.ErrorCode;
import org.springframework.http.HttpStatus;

import static mustodo.backend.enums.error.BasicErrorCode.NOT_AUTHORIZED_USER_ACCESS;

public class NotAuthorizedException extends AuthException {

    private static final ErrorCode errorCode = NOT_AUTHORIZED_USER_ACCESS;

    public NotAuthorizedException() {
        super(errorCode.getErrMsg(), HttpStatus.UNAUTHORIZED, errorCode);
    }
}
