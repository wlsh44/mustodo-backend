package mustodo.backend.dto;

import lombok.Builder;

@Builder
public class MessageDto {

    private String message;
    private Object data;
}
