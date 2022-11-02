package mustodo.backend.exception.todo;

import mustodo.backend.exception.enums.ErrorCode;
import org.springframework.http.HttpStatus;

import static mustodo.backend.exception.enums.TodoErrorCode.INVALID_DATE_FORMAT;

public class InvalidDateFormatException extends TodoException {

    private static final ErrorCode errorCode = INVALID_DATE_FORMAT;

    public InvalidDateFormatException() {
        super(errorCode.getErrMsg(), HttpStatus.BAD_REQUEST, errorCode);
    }
}
