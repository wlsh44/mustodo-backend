package mustodo.backend.exception.user;

import mustodo.backend.exception.enums.ErrorCode;
import org.springframework.http.HttpStatus;

import static mustodo.backend.exception.enums.UserErrorCode.DUPLICATED_EMAIL;

public class EmailDuplicateException extends UserException {

    private static final ErrorCode errorCode = DUPLICATED_EMAIL;

    public EmailDuplicateException(String email) {
        super(String.format(errorCode.getErrMsg(), email), HttpStatus.BAD_REQUEST, errorCode);
    }
}
