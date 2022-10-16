package mustodo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mustodo.backend.enums.error.ErrorCode;
import mustodo.backend.enums.response.ResponseMsg;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private ErrorCode errorCode;

}
