package mustodo.backend.todo.application;

import lombok.AllArgsConstructor;
import mustodo.backend.auth.domain.User;
import mustodo.backend.exception.todo.CategoryNotFoundException;
import mustodo.backend.exception.todo.InvalidRepeatRangeException;
import mustodo.backend.todo.domain.Category;
import mustodo.backend.todo.domain.CategoryRepository;
import mustodo.backend.todo.domain.Todo;
import mustodo.backend.todo.domain.TodoGroup;
import mustodo.backend.todo.domain.TodoGroupRepository;
import mustodo.backend.todo.domain.TodoRepository;
import mustodo.backend.todo.ui.dto.NewRepeatTodoDto;
import mustodo.backend.todo.ui.dto.NewTodoDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final TodoGroupRepository todoGroupRepository;
    private final CategoryRepository categoryRepository;

    public void saveTodo(User user, NewTodoDto dto) {
        Long categoryId = dto.getCategoryId();
        Category category = categoryRepository.findByIdAndUser(categoryId, user)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        saveTodo(dto, user, category);
    }

    @Transactional
    public void saveTodo(NewTodoDto dto, User user, Category category) {
        Todo todo = dto.toEntity(user, category);
        todoRepository.save(todo);
    }

    public void saveRepeatTodo(User user, NewRepeatTodoDto dto) {
        Long categoryId = dto.getCategoryId();
        Category category = categoryRepository.findByIdAndUser(categoryId, user)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        NewRepeatTodoDto.RepeatMeta todoRepeat = dto.getRepeatMeta();
        validateRepeatDateRange(todoRepeat);

        TodoGroup todoGroup = saveTodoGroup(todoRepeat);

        List<DayOfWeek> repeatDay = todoRepeat.getRepeatDay();
        LocalDate date = todoGroup.getStartDate();
        LocalDate endDate = todoGroup.getEndDate();

        while (untilEndDate(date, endDate)) {
            if (repeatDay.contains(date.getDayOfWeek())) {
                saveRepeatTodo(dto, user, category, todoGroup, date);
            }
            date = date.plusDays(1);
        }
    }

    private void validateRepeatDateRange(NewRepeatTodoDto.RepeatMeta todoRepeat) {
        if (isStartDateGreaterThanEqualToEndDate(todoRepeat)) {
            throw new InvalidRepeatRangeException();
        }
    }

    private boolean isStartDateGreaterThanEqualToEndDate(NewRepeatTodoDto.RepeatMeta todoRepeat) {
        return !todoRepeat.getEndDate().isAfter(todoRepeat.getStartDate());
    }

    @Transactional
    public TodoGroup saveTodoGroup(NewRepeatTodoDto.RepeatMeta todoRepeat) {
        TodoGroup todoGroup = TodoGroup.builder()
                .startDate(todoRepeat.getStartDate())
                .endDate(todoRepeat.getEndDate())
                .build();
        todoGroup = todoGroupRepository.save(todoGroup);
        return todoGroup;
    }

    @Transactional
    public void saveRepeatTodo(NewRepeatTodoDto dto, User user, Category category, TodoGroup todoGroup, LocalDate date) {
        Todo todo = dto.toEntity(user, category, date, todoGroup);
        todoRepository.save(todo);
    }

    private boolean untilEndDate(LocalDate date, LocalDate endDate) {
        return !date.isAfter(endDate);
    }
}
