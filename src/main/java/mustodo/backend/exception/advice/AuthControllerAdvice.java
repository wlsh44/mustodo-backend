package mustodo.backend.exception.advice;

import lombok.extern.slf4j.Slf4j;
import mustodo.backend.auth.ui.AuthController;
import mustodo.backend.dto.ErrorResponse;
import mustodo.backend.exception.auth.AuthException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import static mustodo.backend.enums.error.BasicErrorCode.INVALID_ARGUMENT_ERROR;

@Slf4j
@RestControllerAdvice(assignableTypes = {AuthController.class})
public class AuthControllerAdvice {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> bindingException(BindException e) {
        String errMsg = e.getMessage();
        log.error(errMsg);

        return ResponseEntity.badRequest().body(new ErrorResponse(INVALID_ARGUMENT_ERROR));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> userException(AuthException e) {
        String message = e.getMessage();
        log.error(message);

        return ResponseEntity.status(e.getHttpStatus()).body(new ErrorResponse(e.getErrorCode()));
    }
}
