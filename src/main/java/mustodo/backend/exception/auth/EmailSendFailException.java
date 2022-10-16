package mustodo.backend.exception.auth;

import mustodo.backend.enums.error.ErrorCode;
import org.springframework.http.HttpStatus;

import static mustodo.backend.enums.auth.SignUpErrorCode.INVALID_EMAIL_AUTH_KEY;

public class EmailSendFailException extends AuthException {

    private static final ErrorCode errorCode = INVALID_EMAIL_AUTH_KEY;

    public EmailSendFailException() {
        super(errorCode.getErrMsg(), HttpStatus.BAD_REQUEST, errorCode);
    }
}
