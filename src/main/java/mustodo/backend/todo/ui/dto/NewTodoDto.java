package mustodo.backend.todo.ui.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import mustodo.backend.user.domain.User;
import mustodo.backend.todo.domain.Category;
import mustodo.backend.todo.domain.Todo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class NewTodoDto {

    @NotNull
    private Long categoryId;

    @NotBlank
    private String content;

    private boolean dDay;

    private boolean isPublic;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate date;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime alarm;

    public Todo toEntity(User user, Category category) {
        return Todo.builder()
                .content(content)
                .achieve(false)
                .dDay(dDay)
                .date(date)
                .alarm(alarm)
                .isPublic(isPublic)
                .user(user)
                .category(category)
                .build();
    }
}