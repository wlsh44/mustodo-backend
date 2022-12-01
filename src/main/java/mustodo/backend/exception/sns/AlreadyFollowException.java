package mustodo.backend.exception.sns;

import mustodo.backend.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

import static mustodo.backend.exception.errorcode.SnsErrorCode.ALREADY_FOLLOW_ERROR;

public class AlreadyFollowException extends SnsException {

    public static final ErrorCode errorCode = ALREADY_FOLLOW_ERROR;

    public AlreadyFollowException() {
        super(errorCode.getErrMsg(), HttpStatus.BAD_REQUEST, errorCode);
    }
}
