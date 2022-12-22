package mustodo.backend.exception.sns;

import mustodo.backend.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

import static mustodo.backend.exception.errorcode.SnsErrorCode.ALREADY_FOLLOW_ERROR;
import static mustodo.backend.exception.errorcode.SnsErrorCode.NOT_FOLLOWING_USER_ERROR;

public class NotFollowingUserException extends SnsException {

    public static final ErrorCode errorCode = NOT_FOLLOWING_USER_ERROR;

    public NotFollowingUserException() {
        super(errorCode.getErrMsg(), HttpStatus.BAD_REQUEST, errorCode);
    }
}
