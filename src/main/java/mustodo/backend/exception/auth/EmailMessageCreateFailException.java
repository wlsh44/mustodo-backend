package mustodo.backend.exception.auth;

import mustodo.backend.enums.error.ErrorCode;
import org.springframework.http.HttpStatus;

import static mustodo.backend.enums.auth.SignUpErrorCode.PASSWORD_CONFIRM_FAILED;

public class EmailMessageCreateFailException extends AuthException {

    private static final ErrorCode errorCode = PASSWORD_CONFIRM_FAILED;

    public EmailMessageCreateFailException() {
        super(errorCode.getErrMsg(), HttpStatus.INTERNAL_SERVER_ERROR, errorCode);
    }
}
