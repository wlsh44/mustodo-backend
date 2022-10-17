package mustodo.backend.exception.enums;

public enum BasicErrorCode implements ErrorCode {
    INVALID_ARGUMENT_ERROR("올바르지 않은 형식입니다.");

    private final String errMsg;

    BasicErrorCode(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String getErrMsg() {
        return errMsg;
    }
}
