package mustodo.backend.exception.user;

import mustodo.backend.exception.enums.ErrorCode;
import org.springframework.http.HttpStatus;

import static mustodo.backend.exception.enums.AuthErrorCode.EMAIL_NOT_FOUND;

public class UserNotFoundException extends UserException {

    private static final ErrorCode errorCode = EMAIL_NOT_FOUND;

    public UserNotFoundException(String email) {
        super(String.format(errorCode.getErrMsg(), email), HttpStatus.BAD_REQUEST, errorCode);
    }
}
