package mustodo.backend.exception;

import lombok.Getter;
import mustodo.backend.enums.error.ErrorCode;
import mustodo.backend.enums.response.CategoryResponseMsg;
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
