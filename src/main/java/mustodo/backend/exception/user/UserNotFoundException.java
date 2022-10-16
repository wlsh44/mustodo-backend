package mustodo.backend.exception.user;

import mustodo.backend.enums.error.ErrorCode;
import org.springframework.http.HttpStatus;

import static mustodo.backend.enums.auth.SignUpErrorCode.NOT_EXIST_EMAIL;

public class UserNotFoundException extends UserException {

    private static final ErrorCode errorCode = NOT_EXIST_EMAIL;

    public UserNotFoundException(String email) {
        super(String.format(errorCode.getErrMsg(), email), HttpStatus.BAD_REQUEST, errorCode);
    }
}
