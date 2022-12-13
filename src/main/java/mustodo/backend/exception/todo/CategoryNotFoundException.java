package mustodo.backend.exception.todo;

import mustodo.backend.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

import static mustodo.backend.exception.errorcode.TodoErrorCode.CATEGORY_NOT_FOUND;

public class CategoryNotFoundException extends TodoException {

    private static final ErrorCode errorCode = CATEGORY_NOT_FOUND;

    public CategoryNotFoundException(Long id) {
        super(String.format(errorCode.getErrMsg(), id), HttpStatus.BAD_REQUEST, errorCode);
    }
}
