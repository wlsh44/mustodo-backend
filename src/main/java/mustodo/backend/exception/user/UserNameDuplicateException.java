package mustodo.backend.exception.user;

import mustodo.backend.enums.error.ErrorCode;
import org.springframework.http.HttpStatus;

import static mustodo.backend.enums.auth.SignUpErrorCode.ALREADY_EXISTS_NAME;

public class UserNameDuplicateException extends UserException {

    private static final ErrorCode errorCode = ALREADY_EXISTS_NAME;

    public UserNameDuplicateException(String name) {
        super(String.format(errorCode.getErrMsg(), name), HttpStatus.BAD_REQUEST, errorCode);
    }
}
