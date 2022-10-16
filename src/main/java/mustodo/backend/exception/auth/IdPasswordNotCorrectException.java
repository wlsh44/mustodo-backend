package mustodo.backend.exception.auth;

import mustodo.backend.enums.error.ErrorCode;
import org.springframework.http.HttpStatus;

import static mustodo.backend.enums.auth.LoginErrorCode.LOGIN_FAILED_ERROR;

public class IdPasswordNotCorrectException extends AuthException {

    private final static ErrorCode errorCode = LOGIN_FAILED_ERROR;

    public IdPasswordNotCorrectException() {
        super(errorCode.getErrMsg(), HttpStatus.BAD_REQUEST, errorCode);
    }
}