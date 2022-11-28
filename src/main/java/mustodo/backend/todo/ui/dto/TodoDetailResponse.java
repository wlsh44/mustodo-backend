package mustodo.backend.todo.ui.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mustodo.backend.todo.domain.Todo;
import mustodo.backend.todo.domain.TodoGroup;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TodoDetailResponse {

    private Long todoId;

    private Long categoryId;

    private Long groupId;

    private String content;

    private boolean dDay;

    private boolean achieved;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime alarm;

    private LocalDate date;

    public static TodoDetailResponse from(Todo todo) {
        TodoGroup todoGroup = todo.getTodoGroup();
        Long groupId = todoGroup != null ? todoGroup.getId() : null;
        return new TodoDetailResponse(
                todo.getId(),
                todo.getCategory().getId(),
                groupId,
                todo.getContent(),
                todo.isDDay(),
                todo.isAchieve(),
                todo.getAlarm(),
                todo.getDate()
        );
    }
}
