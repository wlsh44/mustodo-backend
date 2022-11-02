package mustodo.backend.todo.ui;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mustodo.backend.user.domain.User;
import mustodo.backend.auth.ui.resolver.Login;
import mustodo.backend.todo.application.TodoService;
import mustodo.backend.todo.ui.dto.NewRepeatTodoDto;
import mustodo.backend.todo.ui.dto.NewTodoDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RequestMapping("/api/todo")
@RestController
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping("")
    public void saveTodo(@Login User user, @RequestBody @Valid NewTodoDto dto) {
        todoService.saveTodo(user, dto);
        log.info(dto.toString());
    }

    @PostMapping("/repeat")
    public void saveRepeatTodo(@Login User user, @RequestBody @Valid NewRepeatTodoDto dto) {
        todoService.saveRepeatTodo(user, dto);
        log.info(dto.toString());
    }
}
