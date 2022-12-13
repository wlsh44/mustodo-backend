package mustodo.backend.exception.auth;

import mustodo.backend.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

import static mustodo.backend.exception.errorcode.AuthErrorCode.EMAIL_SEND_FAILED;

public class EmailSendFailException extends AuthException {

    private static final ErrorCode errorCode = EMAIL_SEND_FAILED;

    public EmailSendFailException() {
        super(errorCode.getErrMsg(), HttpStatus.INTERNAL_SERVER_ERROR, errorCode);
    }
}
