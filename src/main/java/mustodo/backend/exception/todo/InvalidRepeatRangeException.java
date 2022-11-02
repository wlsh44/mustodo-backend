package mustodo.backend.exception.todo;

import mustodo.backend.exception.enums.ErrorCode;
import org.springframework.http.HttpStatus;

import static mustodo.backend.exception.enums.TodoErrorCode.INVALID_REPEAT_RANGE;

public class InvalidRepeatRangeException extends TodoException {

    private static final ErrorCode errorCode = INVALID_REPEAT_RANGE;

    public InvalidRepeatRangeException() {
        super(errorCode.getErrMsg(), HttpStatus.BAD_REQUEST, errorCode);
    }
}
