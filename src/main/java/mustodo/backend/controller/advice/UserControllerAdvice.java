package mustodo.backend.controller.advice;

import lombok.extern.slf4j.Slf4j;
import mustodo.backend.controller.UserController;
import mustodo.backend.dto.MessageDto;
import mustodo.backend.exception.UserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;



import static mustodo.backend.enums.response.BasicResponseMsg.INVALID_ARGUMENT_ERROR;

@Slf4j
@RestControllerAdvice(assignableTypes = {UserController.class})
public class UserControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ResponseEntity<MessageDto> bindingException(BindException e) {
        String errMsg = e.getMessage();
        log.error(errMsg);
        MessageDto message = MessageDto
                .builder()
                .message(INVALID_ARGUMENT_ERROR)
                .build();
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserException.class)
    public ResponseEntity<MessageDto> userException(UserException e) {
        String errMsg = e.getMessage();
        log.error(errMsg);
        MessageDto message = MessageDto
                .builder()
                .message(e.getMsg())
                .build();
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
}