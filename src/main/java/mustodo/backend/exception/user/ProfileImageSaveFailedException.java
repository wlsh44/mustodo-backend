package mustodo.backend.exception.user;

import mustodo.backend.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

import static mustodo.backend.exception.errorcode.UserErrorCode.SAVE_PROFILE_IMAGE_FAILED_ERROR;

public class ProfileImageSaveFailedException extends UserException {

    private static final ErrorCode errorCode = SAVE_PROFILE_IMAGE_FAILED_ERROR;

    public ProfileImageSaveFailedException() {
        super(errorCode.getErrMsg(), HttpStatus.BAD_REQUEST, errorCode);
    }
}
