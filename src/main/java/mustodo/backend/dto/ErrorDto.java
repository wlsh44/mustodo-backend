package mustodo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mustodo.backend.enums.error.ErrorCode;
import mustodo.backend.enums.response.ResponseMsg;

@Getter
@AllArgsConstructor
public class ErrorDto {

    private String message;
    private ErrorCode errorCode;

    public static MessageDtoBuilder builder() {
        return new MessageDtoBuilder();
    }

    public static class MessageDtoBuilder {

        private String message;
        private ErrorCode errorCode;

        public MessageDtoBuilder message(ResponseMsg message) {
            this.message = message.getResMsg();
            return this;
        }

        public MessageDtoBuilder errorCode(ErrorCode errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public ErrorDto build() {
            return new ErrorDto(message, errorCode);
        }
    }
}
