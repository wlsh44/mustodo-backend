package mustodo.backend.exception.advice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mustodo.backend.exception.enums.ErrorCode;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private ErrorCode errorCode;

}
