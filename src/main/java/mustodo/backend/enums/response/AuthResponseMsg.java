package mustodo.backend.enums.response;

public enum AuthResponseMsg implements ResponseMsg {
    SIGN_UP_FAILED("회원가입에 실패했습니다."),
    SIGN_UP_SUCCESS("회원가입에 성공했습니다."),
    EMAIL_AUTH_SEND_FAILED("이메일 인증 전송에 실패했습니다"),
    EMAIL_AUTH_FAILED("이메일 인증에 실패했습니다"),
    EMAIL_AUTH_SUCCESS("이메일 인증에 성공했습니다."),
    LOGIN_SUCCESS("로그인에 성공했습니다."),
    LOGIN_FAILED("로그인에 실패했습니다."),
    LOGOUT_SUCCESS("로그아웃에 성공하였습니다."),
    NOT_AUTHORIZED_USER_ACCESS("권한이 없습니다.");

    private String resMsg;

    AuthResponseMsg(String resMsg) {
        this.resMsg = resMsg;
    }

    @Override
    public String getResMsg() {
        return resMsg;
    }
}
