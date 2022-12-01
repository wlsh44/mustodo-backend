package mustodo.backend.todo.ui.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mustodo.backend.todo.domain.Category;
import mustodo.backend.todo.domain.Todo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TodoByDateResponse {

    private Long categoryId;

    private String categoryName;

    private String color;

    private List<TodoResponse> todoList;

    public static TodoByDateResponse from(Category category, List<Todo> todoList) {
        return new TodoByDateResponse(category.getId(), category.getName(), category.getColor(), TodoResponse.from(todoList));
    }

    public static List<TodoByDateResponse> from(List<Todo> todoList) {
        Map<Category, List<Todo>> categoryTodoList = new HashMap<>();
        todoList.forEach(todo -> categoryTodoList.put(todo.getCategory(), addTodo(categoryTodoList, todo)));
        return categoryTodoList.entrySet()
                .stream()
                .map(entry -> TodoByDateResponse.from(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private static List<Todo> addTodo(Map<Category, List<Todo>> categoryTodoList, Todo todo) {
        List<Todo> todoList = categoryTodoList.getOrDefault(todo.getCategory(), new ArrayList<>());
        todoList.add(todo);
        return todoList;
    }
}
