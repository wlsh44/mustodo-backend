package mustodo.backend.exception.auth;

import mustodo.backend.exception.enums.ErrorCode;
import org.springframework.http.HttpStatus;

import static mustodo.backend.exception.enums.BasicErrorCode.NOT_AUTHORIZED_USER_ACCESS;

public class NotAuthorizedException extends AuthException {

    private static final ErrorCode errorCode = NOT_AUTHORIZED_USER_ACCESS;

    public NotAuthorizedException() {
        super(errorCode.getErrMsg(), HttpStatus.UNAUTHORIZED, errorCode);
    }
}
