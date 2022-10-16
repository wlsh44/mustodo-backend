package mustodo.backend.exception.auth;

import mustodo.backend.exception.enums.ErrorCode;
import org.springframework.http.HttpStatus;

import static mustodo.backend.exception.enums.UserErrorCode.LOGIN_FAILED_ERROR;

public class IdPasswordNotCorrectException extends AuthException {

    private final static ErrorCode errorCode = LOGIN_FAILED_ERROR;

    public IdPasswordNotCorrectException() {
        super(errorCode.getErrMsg(), HttpStatus.BAD_REQUEST, errorCode);
    }
}
