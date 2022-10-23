package mustodo.backend.exception.enums;

public enum TodoErrorCode implements ErrorCode {
    CATEGORY_NOT_FOUND("해당 카테고리가 존재하지 않습니다. id: %d");

    private final String errMsg;

    TodoErrorCode(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String getErrMsg() {
        return errMsg;
    }
}
