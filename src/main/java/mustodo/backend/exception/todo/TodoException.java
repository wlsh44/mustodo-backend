package mustodo.backend.exception.todo;

import lombok.Getter;
import mustodo.backend.exception.enums.ErrorCode;
import mustodo.backend.exception.MustodoException;
import org.springframework.http.HttpStatus;

@Getter
public class TodoException extends MustodoException {

    private final HttpStatus httpStatus;
    private final ErrorCode errorCode;

    public TodoException(String message, HttpStatus httpStatus, ErrorCode errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }
}
