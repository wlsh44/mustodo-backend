package mustodo.backend.exception;

import lombok.Getter;
import mustodo.backend.enums.error.ErrorCode;
import mustodo.backend.enums.response.UserResponseMsg;

@Getter
public class UserException extends RuntimeException {

    private UserResponseMsg msg;
    private ErrorCode errorCode;

    public UserException(UserResponseMsg responseMsg, ErrorCode errorCode) {
        super(errorCode.getErrMsg());
        this.msg = responseMsg;
        this.errorCode = errorCode;
    }
}
