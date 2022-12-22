package mustodo.backend.exception.todo;

import mustodo.backend.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

import static mustodo.backend.exception.errorcode.TodoErrorCode.TODO_NOT_FOUND;

public class TodoNotFoundException extends TodoException {

    private static final ErrorCode errorCode = TODO_NOT_FOUND;

    public TodoNotFoundException(Long id) {
        super(String.format(errorCode.getErrMsg(), id), HttpStatus.BAD_REQUEST, errorCode);
    }
}
