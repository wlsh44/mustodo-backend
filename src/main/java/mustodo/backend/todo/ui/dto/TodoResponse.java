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

    private boolean isDDay;

    private boolean isPublic;

    private String date;

    private long groupId;

    public static TodoResponse from(Todo todo) {
        long groupId = 0L;
        if (todo.getTodoGroup() != null) {
            groupId = todo.getTodoGroup().getId();
        }
        return new TodoResponse(todo.getId(), todo.getContent(), todo.isAchieve(), todo.isDDay(), todo.isPublic(), todo.getDate().toString(), groupId);
    }

    public static List<TodoResponse> from(List<Todo> todoList) {
        return todoList.stream()
                .map(TodoResponse::from)
                .collect(Collectors.toList());
    }
}
