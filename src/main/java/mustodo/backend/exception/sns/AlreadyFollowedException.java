package mustodo.backend.exception.sns;

import mustodo.backend.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

import static mustodo.backend.exception.errorcode.SnsErrorCode.ALREADY_FOLLOW_ERROR;

public class AlreadyFollowedException extends SnsException {

    public static final ErrorCode errorCode = ALREADY_FOLLOW_ERROR;

    public AlreadyFollowedException() {
        super(errorCode.getErrMsg(), HttpStatus.BAD_REQUEST, errorCode);
    }
}
