package mustodo.backend.exception.advice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mustodo.backend.exception.errorcode.ErrorCode;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private ErrorCode errorCode;

}
