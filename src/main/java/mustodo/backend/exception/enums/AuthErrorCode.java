package mustodo.backend.exception.enums;

public enum AuthErrorCode implements ErrorCode {

    PASSWORD_CONFIRM_FAILED("비밀번호 검증에 실패했습니다."),
    UNCHECK_TERMS_AND_CONDITION("약관에 동의하지 않았습니다."),
    EMAIL_SEND_FAILED("이메일 전송에 실패하였습니다."),
    EMAIL_MESSAGE_CREATE_FAILED("이메일 메세지 생성에 실패했습니다."),
    INVALID_EMAIL_AUTH_KEY("올바르지 않은 인증 번호입니다. emailKey: %s"),
    USER_NOT_FOUND("존재하지 않은 이메일입니다. email: %s"),

    UNAUTHORIZED_ACCESS("권한이 없습니다.");

    private final String errMsg;

    AuthErrorCode(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String getErrMsg() {
        return errMsg;
    }
}
