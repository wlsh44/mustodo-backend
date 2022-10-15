package mustodo.backend.enums.error.category;

import mustodo.backend.enums.error.ErrorCode;

public enum NewCategoryErrorCode implements ErrorCode {
    ;

    private String errMsg;

    NewCategoryErrorCode(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String getErrMsg() {
        return errMsg;
    }
}
