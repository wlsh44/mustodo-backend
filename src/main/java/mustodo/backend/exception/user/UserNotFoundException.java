package mustodo.backend.exception.user;

import mustodo.backend.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

import static mustodo.backend.exception.errorcode.AuthErrorCode.USER_NOT_FOUND;

public class UserNotFoundException extends UserException {

    private static final ErrorCode errorCode = USER_NOT_FOUND;

    public UserNotFoundException(String email) {
        super(String.format(errorCode.getErrMsg(), email), HttpStatus.BAD_REQUEST, errorCode);
    }

    public UserNotFoundException(Long id) {
        super(String.format(errorCode.getErrMsg(), id), HttpStatus.BAD_REQUEST, errorCode);
    }
}
