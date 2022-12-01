package mustodo.backend.exception.advice;

import lombok.extern.slf4j.Slf4j;
import mustodo.backend.exception.advice.dto.ErrorResponse;
import mustodo.backend.exception.todo.TodoException;
import mustodo.backend.exception.auth.AuthException;
import mustodo.backend.exception.user.UserException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import javax.validation.ValidationException;

import static mustodo.backend.exception.errorcode.BasicErrorCode.INVALID_ARGUMENT_ERROR;

@Slf4j
@RestControllerAdvice
public class MustodoControllerAdvice {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> bindingException(BindException e) {
        String errMsg = e.getMessage();
        log.error(errMsg);

        return ResponseEntity.badRequest().body(new ErrorResponse(INVALID_ARGUMENT_ERROR));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> bindingException(ValidationException e) {
        String errMsg = e.getMessage();
        log.error(errMsg);

        return ResponseEntity.badRequest().body(new ErrorResponse(INVALID_ARGUMENT_ERROR));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> authException(AuthException e) {
        String message = e.getMessage();
        if (e.getHttpStatus().is5xxServerError()) {
            log.error(message);
        } else {
            log.info(message);
        }

        return ResponseEntity.status(e.getHttpStatus()).body(new ErrorResponse(e.getErrorCode()));
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> userException(UserException e) {
        String message = e.getMessage();
        log.info(message);

        return ResponseEntity.status(e.getHttpStatus()).body(new ErrorResponse(e.getErrorCode()));
    }

    @ExceptionHandler(TodoException.class)
    public ResponseEntity<ErrorResponse> todoException(TodoException e) {
        String message = e.getMessage();
        log.info(message);

        return ResponseEntity.status(e.getHttpStatus()).body(new ErrorResponse(e.getErrorCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exception(Exception e) {
        log.error(e.getMessage());

        return ResponseEntity.internalServerError().build();
    }
}
