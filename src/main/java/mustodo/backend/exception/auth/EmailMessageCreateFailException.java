package mustodo.backend.exception.auth;

import mustodo.backend.exception.enums.ErrorCode;
import org.springframework.http.HttpStatus;

import static mustodo.backend.exception.enums.AuthErrorCode.EMAIL_MESSAGE_CREATE_FAILED;

public class EmailMessageCreateFailException extends AuthException {

    private static final ErrorCode errorCode = EMAIL_MESSAGE_CREATE_FAILED;

    public EmailMessageCreateFailException() {
        super(errorCode.getErrMsg(), HttpStatus.INTERNAL_SERVER_ERROR, errorCode);
    }
}
