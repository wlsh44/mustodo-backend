package mustodo.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mustodo.backend.enums.error.ErrorCode;
import mustodo.backend.enums.response.UserResponseMsg;

@Getter
@AllArgsConstructor
public class UserException extends RuntimeException {

    private UserResponseMsg msg;
    private ErrorCode errorCode;
}
