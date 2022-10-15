package mustodo.backend.exception;

import mustodo.backend.enums.error.ErrorCode;
import mustodo.backend.enums.response.CategoryResponseMsg;

public class CategoryException extends RuntimeException {

    private CategoryResponseMsg msg;

    private ErrorCode errorCode;

    public CategoryException(CategoryResponseMsg msg, ErrorCode errorCode) {
        super(msg.getResMsg());
        this.msg = msg;
        this.errorCode = errorCode;
    }
}
