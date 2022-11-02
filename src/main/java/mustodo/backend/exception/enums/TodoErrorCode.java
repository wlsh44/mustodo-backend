package mustodo.backend.exception.enums;

public enum TodoErrorCode implements ErrorCode {
    CATEGORY_NOT_FOUND("해당 카테고리가 존재하지 않습니다. id: %d"),
    INVALID_REPEAT_RANGE("올바른 반복 범위가 아닙니다."),
    INVALID_DATE_FORMAT("올바른 날짜 형식이 아닙니다.");

    private final String errMsg;

    TodoErrorCode(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String getErrMsg() {
        return errMsg;
    }
}
