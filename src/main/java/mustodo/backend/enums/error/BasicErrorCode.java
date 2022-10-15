package mustodo.backend.enums.error;

public enum BasicErrorCode implements ErrorCode {
    NOT_AUTHORIZED_USER_ACCESS("권한이 없습니다.");

    private String errMsg;

    BasicErrorCode(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String getErrMsg() {
        return errMsg;
    }
}
