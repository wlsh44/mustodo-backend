package mustodo.backend.todo.application;

import lombok.AllArgsConstructor;
import mustodo.backend.exception.todo.InvalidDateFormatException;
import mustodo.backend.exception.todo.TodoNotFoundException;
import mustodo.backend.todo.ui.dto.RepeatMeta;
import mustodo.backend.todo.ui.dto.TodoByDateResponse;
import mustodo.backend.todo.ui.dto.TodoDetailResponse;
import mustodo.backend.todo.ui.dto.TodoResponse;
import mustodo.backend.todo.ui.dto.UpdateTodoDto;
import mustodo.backend.user.domain.User;
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
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@AllArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final TodoGroupRepository todoGroupRepository;
    private final CategoryRepository categoryRepository;

    public void saveTodo(User user, NewTodoDto dto) {
        Long categoryId = dto.getCategoryId();
        Category category = categoryRepository.findByUserAndId(user, categoryId)
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
        Category category = categoryRepository.findByUserAndId(user, categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        RepeatMeta todoRepeat = dto.getRepeatMeta();
        validateRepeatDateRange(todoRepeat);

        TodoGroup todoGroup = saveTodoGroup(user, todoRepeat);

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

    private void validateRepeatDateRange(RepeatMeta todoRepeat) {
        if (isStartDateGreaterThanEqualToEndDate(todoRepeat)) {
            throw new InvalidRepeatRangeException();
        }
    }

    private boolean isStartDateGreaterThanEqualToEndDate(RepeatMeta todoRepeat) {
        return !todoRepeat.getEndDate().isAfter(todoRepeat.getStartDate());
    }

    @Transactional
    public TodoGroup saveTodoGroup(User user, RepeatMeta todoRepeat) {
        TodoGroup todoGroup = TodoGroup.builder()
                .startDate(todoRepeat.getStartDate())
                .endDate(todoRepeat.getEndDate())
                .user(user)
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

    @Transactional(readOnly = true)
    public List<TodoResponse> findByCategory(User user, Long categoryId) {
        if (!categoryRepository.existsByUserAndId(user, categoryId)) {
            throw new CategoryNotFoundException(categoryId);
        }
        List<Todo> todoList = todoRepository.findAllByCategory_Id(categoryId);

        return TodoResponse.from(todoList);
    }

    @Transactional(readOnly = true)
    public List<TodoByDateResponse> findByDate(User user, String dateString) {
        LocalDate date = parseDate(dateString);
        List<Todo> todoList = todoRepository.findAllByUserAndDate(user, date);

        return TodoByDateResponse.from(todoList);
    }

    private LocalDate parseDate(String dateString) {
        try {
            return LocalDate.parse(dateString);
        } catch (DateTimeParseException e) {
            throw new InvalidDateFormatException();
        }
    }

    @Transactional
    public void deleteTodo(User user, Long todoId) {
        Todo todo = todoRepository.findByUserAndId(user, todoId)
                .orElseThrow(() -> new TodoNotFoundException(todoId));
        todoRepository.delete(todo);
    }

    @Transactional
    public void checkAchieve(User user, Long todoId) {
        Todo todo = todoRepository.findByUserAndId(user, todoId)
                .orElseThrow(() -> new TodoNotFoundException(todoId));
        todo.checkAchieve();
    }

    @Transactional(readOnly = true)
    public TodoDetailResponse findById(User user, Long todoId) {
        Todo todo = todoRepository.findByUserAndId(user, todoId)
                .orElseThrow(() -> new TodoNotFoundException(todoId));
        return TodoDetailResponse.from(todo);
    }

    @Transactional
    public void update(User user, Long todoId, UpdateTodoDto dto) {
        Todo todo = todoRepository.findByUserAndId(user, todoId)
                .orElseThrow(() -> new TodoNotFoundException(todoId));
        Category newCategory = categoryRepository.findByUserAndId(user, dto.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(dto.getCategoryId()));

        todo.update(dto, newCategory);
    }
}
