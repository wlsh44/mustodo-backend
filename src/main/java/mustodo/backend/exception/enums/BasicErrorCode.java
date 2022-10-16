package mustodo.backend.exception.enums;

public enum BasicErrorCode implements ErrorCode {
    NOT_AUTHORIZED_USER_ACCESS("권한이 없습니다."),
    INVALID_ARGUMENT_ERROR("올바르지 않은 형식입니다.");

    private String errMsg;

    BasicErrorCode(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String getErrMsg() {
        return errMsg;
    }
}
