package mustodo.backend.todo.ui;

import lombok.RequiredArgsConstructor;
import mustodo.backend.todo.ui.dto.TodoByDateResponse;
import mustodo.backend.todo.ui.dto.TodoDetailResponse;
import mustodo.backend.todo.ui.dto.TodoMonthResponse;
import mustodo.backend.todo.ui.dto.TodoResponse;
import mustodo.backend.todo.ui.dto.UpdateTodoDto;
import mustodo.backend.user.domain.User;
import mustodo.backend.auth.ui.resolver.Login;
import mustodo.backend.todo.application.TodoService;
import mustodo.backend.todo.ui.dto.NewRepeatTodoDto;
import mustodo.backend.todo.ui.dto.NewTodoDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/api/todo")
@RestController
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping("")
    public void saveTodo(@Login User user, @RequestBody @Valid NewTodoDto dto) {
        todoService.saveTodo(user, dto);
    }

    @PostMapping("/repeat")
    public void saveRepeatTodo(@Login User user, @RequestBody @Valid NewRepeatTodoDto dto) {
        todoService.saveRepeatTodo(user, dto);
    }

    @GetMapping("")
    public ResponseEntity<List<TodoResponse>> findByCategory(@Login User user, @RequestParam Long categoryId) {
        List<TodoResponse> todoResponseList = todoService.findByCategory(user, categoryId);

        return ResponseEntity.ok(todoResponseList);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<TodoByDateResponse>> findByDate(@Login User user, @PathVariable String date) {
        List<TodoByDateResponse> todoResponseList = todoService.findByDate(user, date);

        return ResponseEntity.ok(todoResponseList);
    }

    @GetMapping("/month/{date}")
    public ResponseEntity<List<TodoMonthResponse>> findByMonth(@Login User user, @PathVariable String date) {
        List<TodoMonthResponse> response = todoService.findTodoMonthByDate(user, date);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{todoId}")
    public void deleteTodo(@Login User user, @PathVariable Long todoId) {
        todoService.deleteTodo(user, todoId);
    }

    @PatchMapping("/{todoId}")
    public void checkAchieve(@Login User user, @PathVariable Long todoId) {
        todoService.checkAchieve(user, todoId);
    }

    @GetMapping("/{todoId}")
    public ResponseEntity<TodoDetailResponse> findById(@Login User user, @PathVariable Long todoId) {
        TodoDetailResponse todoDetailResponse = todoService.findById(user, todoId);

        return ResponseEntity.ok(todoDetailResponse);
    }

    @PutMapping("/{todoId}")
    public void update(@Login User user, @PathVariable Long todoId, @RequestBody UpdateTodoDto dto) {
        todoService.update(user, todoId, dto);
    }
}
