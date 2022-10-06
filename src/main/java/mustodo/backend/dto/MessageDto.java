package mustodo.backend.dto;

import lombok.Builder;
import mustodo.backend.enums.response.ResponseMsg;

@Builder
public class MessageDto {

    private ResponseMsg message;
    private Object data;
}
