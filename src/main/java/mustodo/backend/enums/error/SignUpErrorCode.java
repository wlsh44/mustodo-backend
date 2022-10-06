package mustodo.backend.enums.error;

public enum SignUpErrorCode implements ErrorCode {
    ALREADY_EXIST_EMAIL("이미 존재하는 이메일 입니다."),
    PASSWORD_CONFIRM_FAILED("비밀번호 검증에 실패했습니다."),
    UNCHECK_TERMS_AND_CONDITION("약관에 동의하지 않았습니다.");

    private String errMsg;

    SignUpErrorCode(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String getErrMsg() {
        return errMsg;
    }
}