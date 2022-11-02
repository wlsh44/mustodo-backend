package mustodo.backend.todo.ui.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mustodo.backend.todo.domain.Todo;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TodoResponse {

    private long todoId;

    private String content;

    private boolean isAchieved;

    public static TodoResponse from(Todo todo) {
        return new TodoResponse(todo.getId(), todo.getContent(), todo.isAchieve());
    }
}
