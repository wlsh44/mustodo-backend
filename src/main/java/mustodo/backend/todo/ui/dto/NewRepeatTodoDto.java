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
import mustodo.backend.todo.domain.TodoGroup;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class NewRepeatTodoDto {

    @NotNull
    private Long categoryId;

    @NotBlank
    private String content;

    private boolean dDay;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime alarm;

    @NotNull
    private RepeatMeta repeatMeta;

    public Todo toEntity(User user, Category category, LocalDate date, TodoGroup todoGroup) {
        return Todo.builder()
                .content(content)
                .achieve(false)
                .dDay(dDay)
                .date(date)
                .alarm(alarm)
                .todoGroup(todoGroup)
                .user(user)
                .category(category)
                .build();
    }
}
