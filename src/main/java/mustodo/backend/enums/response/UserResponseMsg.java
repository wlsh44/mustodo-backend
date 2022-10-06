package mustodo.backend.enums.response;

public enum UserResponseMsg implements ResponseMsg {
    SIGN_UP_FAILED("회원가입에 실패했습니다."),
    SIGN_UP_SUCCESS("회원가입에 성공했습니다."),
    EMAIL_AUTH_FAILED("이메일 인증에 실패했습니다");

    private String resMsg;

    UserResponseMsg(String resMsg) {
        this.resMsg = resMsg;
    }

    @Override
    public String getResMsg() {
        return resMsg;
    }
}
