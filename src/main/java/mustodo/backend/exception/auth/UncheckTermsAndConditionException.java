package mustodo.backend.exception.auth;

import mustodo.backend.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

import static mustodo.backend.exception.errorcode.AuthErrorCode.UNCHECK_TERMS_AND_CONDITION;

public class UncheckTermsAndConditionException extends AuthException {

    private static final ErrorCode errorCode = UNCHECK_TERMS_AND_CONDITION;

    public UncheckTermsAndConditionException() {
        super(errorCode.getErrMsg(), HttpStatus.BAD_REQUEST, errorCode);
    }
}
