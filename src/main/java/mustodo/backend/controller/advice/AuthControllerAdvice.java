package mustodo.backend.controller.advice;

import lombok.extern.slf4j.Slf4j;
import mustodo.backend.controller.AuthController;
import mustodo.backend.dto.ErrorDto;
import mustodo.backend.enums.error.ErrorCode;
import mustodo.backend.enums.response.AuthResponseMsg;
import mustodo.backend.exception.AuthException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import static mustodo.backend.enums.error.LoginErrorCode.LOGIN_FAILED_ERROR;
import static mustodo.backend.enums.error.LoginErrorCode.NOT_EXIST_EMAIL;
import static mustodo.backend.enums.error.LoginErrorCode.PASSWORD_NOT_CORRECT;
import static mustodo.backend.enums.response.BasicResponseMsg.INVALID_ARGUMENT_ERROR;

@Slf4j
@RestControllerAdvice(assignableTypes = {AuthController.class})
public class AuthControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorDto> bindingException(BindException e) {
        String errMsg = e.getMessage();
        log.error(errMsg);
        ErrorDto message = ErrorDto
                .builder()
                .message(INVALID_ARGUMENT_ERROR)
                .build();
        return ResponseEntity.badRequest().body(message);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorDto> userException(AuthException e) {
        ErrorCode errorCode = e.getErrorCode();
        AuthResponseMsg msg = e.getMsg();

        log.error(errorCode.getErrMsg());

        if (PASSWORD_NOT_CORRECT.equals(errorCode) || NOT_EXIST_EMAIL.equals(errorCode)) {
            errorCode = LOGIN_FAILED_ERROR;
        }

        ErrorDto message = ErrorDto
                .builder()
                .message(msg)
                .errorCode(errorCode)
                .build();
        return ResponseEntity.badRequest().body(message);
    }
}
