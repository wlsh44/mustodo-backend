package mustodo.backend.exception.user;

import mustodo.backend.exception.enums.ErrorCode;
import org.springframework.http.HttpStatus;

import static mustodo.backend.exception.enums.UserErrorCode.DUPLICATED_USER_NAME;

public class UserNameDuplicateException extends UserException {

    private static final ErrorCode errorCode = DUPLICATED_USER_NAME;

    public UserNameDuplicateException(String name) {
        super(String.format(errorCode.getErrMsg(), name), HttpStatus.BAD_REQUEST, errorCode);
    }
}
