package mustodo.backend.exception.errorcode;

public enum SnsErrorCode implements ErrorCode{
    ALREADY_FOLLOW_ERROR("이미 팔로우 하고있는 유저입니다."),
    ACTION_NOT_FOUND("해당 액션이 존재하지 않습니다. action : %s");

    private final String errMsg;

    SnsErrorCode(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String getErrMsg() {
        return errMsg;
    }
}
