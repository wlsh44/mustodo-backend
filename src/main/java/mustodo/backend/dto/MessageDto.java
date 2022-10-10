package mustodo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mustodo.backend.enums.response.ResponseMsg;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {

    private String message;
    private Object data;

    public static MessageDtoBuilder builder() {
        return new MessageDtoBuilder();
    }

    public static class MessageDtoBuilder {

        private String message;
        private Object data;

        public MessageDtoBuilder message(ResponseMsg message) {
            this.message = message.getResMsg();
            return this;
        }

        public MessageDtoBuilder data(Object data) {
            this.data = data;
            return this;
        }

        public MessageDto build() {
            return new MessageDto(message, data);
        }
    }
}
