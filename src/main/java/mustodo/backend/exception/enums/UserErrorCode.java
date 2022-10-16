package mustodo.backend.exception.enums;

public enum UserErrorCode implements ErrorCode {
    ALREADY_EXISTS_EMAIL("이미 존재하는 이메일 입니다. email: %s"),
    ALREADY_EXISTS_NAME("이미 존재하는 이름 입니다. name: %s"),
    LOGIN_FAILED_ERROR("아이디 또는 비밀번호가 틀렸습니다.");

    private String errMsg;

    UserErrorCode(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String getErrMsg() {
        return errMsg;
    }
}
