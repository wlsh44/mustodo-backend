package mustodo.backend.enums.error;

public enum LoginErrorCode implements ErrorCode {
    PASSWORD_NOT_CORRECT("비밀번호가 올바르지 않습니다."),
    NOT_EXIST_EMAIL("존재하지 않은 이메일입니다."),
    NOT_AUTHORIZED_USER("인증되지 않은 유저입니다.");

    private String errMsg;

    LoginErrorCode(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String getErrMsg() {
        return errMsg;
    }
}
