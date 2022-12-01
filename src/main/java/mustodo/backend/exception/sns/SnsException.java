package mustodo.backend.exception.sns;

import lombok.Getter;
import mustodo.backend.exception.MustodoException;
import mustodo.backend.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
public class SnsException extends MustodoException {

    private final HttpStatus httpStatus;
    private final ErrorCode errorCode;

    public SnsException(String message, HttpStatus httpStatus, ErrorCode errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }
}