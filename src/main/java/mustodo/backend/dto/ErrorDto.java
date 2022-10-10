package mustodo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mustodo.backend.enums.error.ErrorCode;
import mustodo.backend.enums.response.ResponseMsg;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDto {

    private String message;
    private ErrorCode errorCode;

    public static ErrorDtoBuilder builder() {
        return new ErrorDtoBuilder();
    }

    public static class ErrorDtoBuilder {

        private String message;
        private ErrorCode errorCode;

        public ErrorDtoBuilder message(ResponseMsg message) {
            this.message = message.getResMsg();
            return this;
        }

        public ErrorDtoBuilder errorCode(ErrorCode errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public ErrorDto build() {
            return new ErrorDto(message, errorCode);
        }
    }
}
