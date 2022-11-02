package mustodo.backend.todo.ui.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mustodo.backend.todo.domain.Todo;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TodoResponse {

    private long todoId;

    private String content;

    private boolean isAchieved;

    public static TodoResponse from(Todo todo) {
        return new TodoResponse(todo.getId(), todo.getContent(), todo.isAchieve());
    }

    public static List<TodoResponse> from(List<Todo> todoList) {
        return todoList.stream()
                .map(TodoResponse::from)
                .collect(Collectors.toList());
    }
}
