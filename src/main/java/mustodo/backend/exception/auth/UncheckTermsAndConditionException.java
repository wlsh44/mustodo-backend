package mustodo.backend.exception.auth;

import mustodo.backend.enums.error.ErrorCode;
import org.springframework.http.HttpStatus;

import static mustodo.backend.enums.auth.SignUpErrorCode.PASSWORD_CONFIRM_FAILED;
import static mustodo.backend.enums.auth.SignUpErrorCode.UNCHECK_TERMS_AND_CONDITION;

public class UncheckTermsAndConditionException extends AuthException {

    private static final ErrorCode errorCode = UNCHECK_TERMS_AND_CONDITION;

    public UncheckTermsAndConditionException() {
        super(errorCode.getErrMsg(), HttpStatus.BAD_REQUEST, errorCode);
    }
}
