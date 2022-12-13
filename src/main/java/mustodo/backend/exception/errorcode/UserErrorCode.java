package mustodo.backend.exception.errorcode;

public enum UserErrorCode implements ErrorCode {
    DUPLICATED_EMAIL("이미 존재하는 이메일 입니다. email: %s"),
    DUPLICATED_USER_NAME("이미 존재하는 이름 입니다. name: %s"),
    LOGIN_FAILED_ERROR("아이디 또는 비밀번호가 틀렸습니다."),
    SAVE_PROFILE_IMAGE_FAILED_ERROR("프로필 이미지 저장에 실패했습니다.");

    private final String errMsg;

    UserErrorCode(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String getErrMsg() {
        return errMsg;
    }
}
