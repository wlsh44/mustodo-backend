package mustodo.backend.dto;

import lombok.Builder;
import lombok.Getter;
import mustodo.backend.enums.response.ResponseMsg;

@Getter
@Builder
public class MessageDto {

    private ResponseMsg message;
    private Object data;
}
