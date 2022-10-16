package mustodo.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MustodoException extends RuntimeException {

    private final HttpStatus httpStatus;

    public MustodoException(String message) {
        super(message);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
