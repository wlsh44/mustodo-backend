package mustodo.backend.exception;

import lombok.Getter;
import mustodo.backend.enums.error.ErrorCode;
import mustodo.backend.enums.response.AuthResponseMsg;

@Getter
public class AuthException extends RuntimeException {

    private AuthResponseMsg msg;
    private ErrorCode errorCode;

    public AuthException(AuthResponseMsg responseMsg, ErrorCode errorCode) {
        super(errorCode.getErrMsg());
        this.msg = responseMsg;
        this.errorCode = errorCode;
    }
}
