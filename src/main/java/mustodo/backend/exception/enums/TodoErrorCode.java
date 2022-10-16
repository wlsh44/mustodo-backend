package mustodo.backend.exception.enums;

public enum TodoErrorCode implements ErrorCode {
    ;

    private final String errMsg;

    TodoErrorCode(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String getErrMsg() {
        return errMsg;
    }
}
