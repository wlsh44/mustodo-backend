package mustodo.backend.todo.application;

import mustodo.backend.exception.todo.InvalidDateFormatException;
import mustodo.backend.exception.todo.TodoNotFoundException;
import mustodo.backend.todo.ui.dto.RepeatMeta;
import mustodo.backend.todo.ui.dto.TodoByDateResponse;
import mustodo.backend.todo.ui.dto.TodoDetailResponse;
import mustodo.backend.todo.ui.dto.TodoResponse;
import mustodo.backend.todo.ui.dto.UpdateTodoDto;
import mustodo.backend.user.domain.Role;
import mustodo.backend.user.domain.User;
import mustodo.backend.user.domain.embedded.EmailAuth;
import mustodo.backend.common.support.DatabaseClearer;
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
import mustodo.backend.user.domain.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class TodoServiceTest {

    @Autowired
    TodoService todoService;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    TodoRepository todoRepository;

    @Autowired
    TodoGroupRepository todoGroupRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DatabaseClearer databaseClearer;

    @Autowired
    PasswordEncoder passwordEncoder;

    User user;

    Category category;

    @BeforeEach
    void init() {
        user = User.builder()
                .name("test")
                .email("test@test.test")
                .password(passwordEncoder.encode("test"))
                .emailAuth(new EmailAuth("123456", true))
                .role(Role.USER)
                .build();
        category = Category.builder()
                .name("test category")
                .user(user)
                .color("#000000")
                .publicAccess(false)
                .build();
        user = userRepository.save(user);
        category = categoryRepository.save(category);
    }

    @AfterEach
    void clear() {
        databaseClearer.clear();
    }

    @Nested
    @DisplayName("할 일 생성 테스트")
    class saveTest {

        @Test
        @DisplayName("생성 성공")
        void saveSuccess() {
            //given
            LocalDate localDateNow = LocalDate.now();
            LocalDateTime localDateTimeNow = LocalDateTime.now();
            NewTodoDto newTodoDto = new NewTodoDto(1L, "할 일", false, localDateNow, localDateTimeNow);
            List<Todo> expect = List.of(new Todo(1L, "할 일", false, false, localDateTimeNow, localDateNow, category, user, null));

            //when
            todoService.saveTodo(user, newTodoDto);

            //then
            List<Todo> todos = todoRepository.findAll();

            assertThat(todos).isEqualTo(expect);
        }

        @Test
        @DisplayName("생성 실패 - 카테고리 못 찾는 경우")
        void saveFail_categoryNotFound() {
            //given
            long notExistId = 2L;
            CategoryNotFoundException e = new CategoryNotFoundException(notExistId);
            NewTodoDto newTodoDto = new NewTodoDto(notExistId, "할 일", false, LocalDate.now(), LocalDateTime.now());

            //when then
            assertThatThrownBy(() -> todoService.saveTodo(user, newTodoDto))
                    .isInstanceOf(e.getClass())
                    .hasMessage(e.getMessage());
        }
    }

    @Nested
    @DisplayName("반복 할 일 생성 테스트")
    class NewRepeatTodoTest {

        @Test
        @DisplayName("생성 성공")
        void saveSuccess() {
            //given
            LocalDateTime localDateTimeNow = LocalDateTime.now();
            LocalDate startDate = LocalDate.of(2022, 10, 1);
            LocalDate endDate = LocalDate.of(2022, 10, 15);
            NewRepeatTodoDto newTodoDto = new NewRepeatTodoDto(1L, "할 일", false, localDateTimeNow,
                    new RepeatMeta(List.of(DayOfWeek.MONDAY, DayOfWeek.SATURDAY), startDate, endDate));
            TodoGroup todoGroup = new TodoGroup(1L, startDate, endDate, user);
            List<TodoGroup> expectTodoGroup = List.of(todoGroup);
            List<Todo> expectTodoList = List.of(
                    new Todo(1L, "할 일", false, false, localDateTimeNow, LocalDate.of(2022, 10, 1), category, user, todoGroup),
                    new Todo(2L, "할 일", false, false, localDateTimeNow, LocalDate.of(2022, 10, 3), category, user, todoGroup),
                    new Todo(3L, "할 일", false, false, localDateTimeNow, LocalDate.of(2022, 10, 8), category, user, todoGroup),
                    new Todo(4L, "할 일", false, false, localDateTimeNow, LocalDate.of(2022, 10, 10), category, user, todoGroup),
                    new Todo(5L, "할 일", false, false, localDateTimeNow, LocalDate.of(2022, 10, 15), category, user, todoGroup)
            );

            //when
            todoService.saveRepeatTodo(user, newTodoDto);

            //then
            List<TodoGroup> todoGroups = todoGroupRepository.findAll();
            List<Todo> todos = todoRepository.findAll();

            assertThat(todoGroups).isEqualTo(expectTodoGroup);
            assertThat(todos).isEqualTo(expectTodoList);
        }

        @Test
        @DisplayName("생성 실패")
        void saveFail_startDateLargeThanEndDay() {
            //given
            InvalidRepeatRangeException e = new InvalidRepeatRangeException();
            NewRepeatTodoDto newTodoDto = new NewRepeatTodoDto(1L, "할 일", false, LocalDateTime.now(),
                    new RepeatMeta(List.of(DayOfWeek.MONDAY, DayOfWeek.SATURDAY),
                            LocalDate.of(2022, 10, 15),
                            LocalDate.of(2022, 10, 1)));

            //when then
            assertThatThrownBy(() -> todoService.saveRepeatTodo(user, newTodoDto))
                    .isInstanceOf(e.getClass())
                    .hasMessage(e.getMessage());
        }

        @Test
        @DisplayName("생성 실패 - 카테고리 못 찾는 경우")
        void saveFail_categoryNotFound() {
            //given
            long notExistId = 2L;
            CategoryNotFoundException e = new CategoryNotFoundException(notExistId);
            NewRepeatTodoDto newTodoDto = new NewRepeatTodoDto(notExistId, "할 일", false, LocalDateTime.now(),
                    new RepeatMeta(List.of(DayOfWeek.MONDAY, DayOfWeek.SATURDAY), LocalDate.now(), LocalDate.now().plusDays(1)));

            //when then
            assertThatThrownBy(() -> todoService.saveRepeatTodo(user, newTodoDto))
                    .isInstanceOf(e.getClass())
                    .hasMessage(e.getMessage());
        }
    }

    @Nested
    @DisplayName("카테고리 별 할 일 조회 테스트")
    class FindAllByCategoryId {

        @Test
        @DisplayName("조회 성공")
        void success() {
            //given
            List<TodoResponse> expect = List.of(
                    TodoResponse.from(todoRepository.save(new Todo(1L, "할 일1", false, false, LocalDateTime.now(), LocalDate.now(), category, user, null))),
                    TodoResponse.from(todoRepository.save(new Todo(2L, "할 일2", false, false, LocalDateTime.now(), LocalDate.now(), category, user, null))),
                    TodoResponse.from(todoRepository.save(new Todo(3L, "할 일3", false, false, LocalDateTime.now(), LocalDate.now(), category, user, null))));

            //when
            List<TodoResponse> res = todoService.findByCategory(user, category.getId());

            //then
            assertThat(res).usingRecursiveComparison()
                    .isEqualTo(expect);
        }

        @Test
        @DisplayName("조회 실패 - 없는 카테고리")
        void fail_categoryNotFound() {
            //given
            long notExistId = 2L;
            CategoryNotFoundException e = new CategoryNotFoundException(notExistId);
            todoRepository.save(new Todo(1L, "할 일1", false, false, LocalDateTime.now(), LocalDate.now(), category, user, null));
            todoRepository.save(new Todo(2L, "할 일2", false, false, LocalDateTime.now(), LocalDate.now(), category, user, null));
            todoRepository.save(new Todo(3L, "할 일3", false, false, LocalDateTime.now(), LocalDate.now(), category, user, null));

            //when then
            assertThatThrownBy(() -> todoService.findByCategory(user, notExistId))
                    .isInstanceOf(e.getClass())
                    .hasMessage(e.getMessage());
        }
    }

    @Nested
    @DisplayName("날짜 별 조회 테스트")
    class FindByDateTest {

        Todo todo1_20221015;
        Todo todo2_20221015;
        Todo todo3_20221015;
        Todo todo4_20221017;
        Todo todo5_20221015;

        Category category2;

        @BeforeEach
        void init() {
            category2 = Category.builder()
                    .name("category2")
                    .user(user)
                    .color("#FFFFFF")
                    .publicAccess(true)
                    .build();
            category2 = categoryRepository.save(category2);
            todo1_20221015 = todoRepository.save(new Todo(1L, "할 일1", false, false, LocalDateTime.now(), LocalDate.of(2022, 10, 15), category, user, null));
            todo2_20221015 = todoRepository.save(new Todo(2L, "할 일2", false, false, LocalDateTime.now(), LocalDate.of(2022, 10, 15), category2, user, null));
            todo3_20221015 = todoRepository.save(new Todo(3L, "할 일3", false, false, LocalDateTime.now(), LocalDate.of(2022, 10, 15), category, user, null));
            todo4_20221017 = todoRepository.save(new Todo(4L, "할 일4", false, false, LocalDateTime.now(), LocalDate.of(2022, 10, 17), category, user, null));
            todo5_20221015 = todoRepository.save(new Todo(4L, "할 일4", false, true, LocalDateTime.now(), LocalDate.of(2022, 10, 15), category, user, null));
        }

        @Test
        @DisplayName("조회 성공")
        void success() {
            //given
            String dateString = "2022-10-15";
            List<TodoByDateResponse> expect = TodoByDateResponse.from(List.of(todo1_20221015, todo2_20221015, todo3_20221015, todo5_20221015));

            //when
            List<TodoByDateResponse> res = todoService.findByDate(user, dateString);

            //then
            assertThat(res).usingRecursiveComparison()
                    .isEqualTo(expect);
        }

        @Test
        @DisplayName("조회 실패 - 잘못된 날짜 형식")
        void fail_invalidDateFormat() {
            //given
            String wrongDateFormat = "wrongDateFormat";
            InvalidDateFormatException e = new InvalidDateFormatException();

            //when then
            assertThatThrownBy(() -> todoService.findByDate(user, wrongDateFormat))
                    .isInstanceOf(e.getClass())
                    .hasMessage(e.getMessage());
        }
    }

    @Nested
    @DisplayName("할 일 삭제 테스트")
    class DeleteTodoTest {

        Todo todo1;
        Todo todo2;
        Todo todo3;

        @BeforeEach
        void init() {
            todo1 = todoRepository.save(new Todo(1L, "할 일1", false, false, LocalDateTime.now(), LocalDate.of(2022, 10, 15), category, user, null));
            todo2 = todoRepository.save(new Todo(2L, "할 일2", false, false, LocalDateTime.now(), LocalDate.of(2022, 10, 15), category, user, null));
            todo3 = todoRepository.save(new Todo(3L, "할 일3", false, false, LocalDateTime.now(), LocalDate.of(2022, 10, 15), category, user, null));
        }

        @Test
        @DisplayName("삭제 성공")
        void deleteSuccess() {
            //given
            Long todoId = 1L;
            List<Todo> expect = List.of(todo2, todo3);

            //when
            todoService.deleteTodo(user, todoId);

            //then
            List<Todo> todoList = todoRepository.findAll();
            assertThat(todoList).isEqualTo(expect);
        }

        @Test
        @DisplayName("삭제 실패 - 할 일 없음")
        void deleteFail_todoNotFound() {
            //given
            Long todoId = 4L;
            TodoNotFoundException e = new TodoNotFoundException(todoId);

            //when then
            assertThatThrownBy(() -> todoService.deleteTodo(user, todoId))
                    .isInstanceOf(e.getClass())
                    .hasMessage(e.getMessage());
        }
    }

    @Nested
    @DisplayName("checkAchieve 테스트")
    class CheckAchieveTest {

        Todo todo1;

        @BeforeEach
        void init() {
            todo1 = todoRepository.save(new Todo(1L, "할 일1", false, false, LocalDateTime.now(), LocalDate.of(2022, 10, 15), category, user, null));
        }

        @Test
        @DisplayName("할 일 체크 성공")
        void successTest() {
            //when
            todoService.checkAchieve(user, 1L);

            //then
            Todo todo = todoRepository.findById(1L).get();
            assertThat(todo.isAchieve()).isTrue();
        }

        @Test
        @DisplayName("할 일 체크 실패 - 없는 할 일")
        void failTest_todoNotFound() {
            //given
            Long todoId = 2L;
            TodoNotFoundException e = new TodoNotFoundException(todoId);

            //when then
            assertThatThrownBy(() -> todoService.checkAchieve(user, todoId))
                    .isInstanceOf(e.getClass())
                    .hasMessage(e.getMessage());
        }
    }

    @Nested
    @DisplayName("findById 테스트")
    class FindById {

        Todo todo1;
        LocalDateTime now;
        @BeforeEach
        void init() {
            now = LocalDateTime.now();
            todo1 = todoRepository.save(new Todo(1L, "할 일1", false, false, now, LocalDate.of(2022, 10, 15), category, user, null));
        }

        @Test
        @DisplayName("할 일 조회 성공")
        void successTest() {
            //given
            Long todoId = 1L;
            TodoDetailResponse expect = new TodoDetailResponse(todoId, category.getId(), null, "할 일1", false, false, now, LocalDate.of(2022, 10, 15));
            //when
            TodoDetailResponse res = todoService.findById(user, todoId);

            //then
            assertThat(res).usingRecursiveComparison()
                    .isEqualTo(expect);
        }

        @Test
        @DisplayName("할 일 조회 실패 - 없는 할 일")
        void failTest_todoNotFound() {
            //given
            Long todoId = 2L;
            TodoNotFoundException e = new TodoNotFoundException(todoId);

            //when then
            assertThatThrownBy(() -> todoService.findById(user, todoId))
                    .isInstanceOf(e.getClass())
                    .hasMessage(e.getMessage());
        }
    }

    @Nested
    @DisplayName("update 테스트")
    @Transactional
    class UpdateTest {

        Todo todo1;
        Category category2;

        @BeforeEach
        void init() {
            todo1 = todoRepository.save(new Todo(1L, "할 일1", false, false, LocalDateTime.now(), LocalDate.of(2022, 10, 15), category, user, null));
            category2 = Category.builder()
                    .name("test category")
                    .user(user)
                    .color("#000000")
                    .publicAccess(false)
                    .build();
            category2 = categoryRepository.save(category2);
        }

        @Test
        @DisplayName("할 일 수정 성공")
        void successTest() {
            //given
            LocalDateTime nowDateTime = LocalDateTime.now();
            LocalDate nowDate = LocalDate.now();
            String newContent = "할 일33";
            UpdateTodoDto dto = new UpdateTodoDto(category2.getId(), newContent, true, nowDateTime, nowDate);
            Todo expect = new Todo(1L, newContent, false, true, nowDateTime, nowDate, category2, user, null);

            //when
            todoService.update(user, 1L, dto);

            //then
            Todo res = todoRepository.findById(1L).get();
            assertThat(res).usingRecursiveComparison()
                    .isEqualTo(expect);
        }

        @Test
        @DisplayName("할 일 수정 실패 - 없는 할 일")
        void failTest_todoNotFound() {
            //given
            Long todoId = 2L;
            UpdateTodoDto dto = new UpdateTodoDto(category.getId(), "new content", true, null, null);
            TodoNotFoundException e = new TodoNotFoundException(todoId);

            //when then
            assertThatThrownBy(() -> todoService.update(user, todoId, dto))
                    .isInstanceOf(e.getClass())
                    .hasMessage(e.getMessage());
        }

        @Test
        @DisplayName("할 일 수정 실패 - 없는 카테고리")
        void failTest_categoryNotFound() {
            //given
            Long categoryId = 3L;
            UpdateTodoDto dto = new UpdateTodoDto(categoryId, "new content", true, null, null);
            CategoryNotFoundException e = new CategoryNotFoundException(categoryId);

            //when then
            assertThatThrownBy(() -> todoService.update(user, todo1.getId(), dto))
                    .isInstanceOf(e.getClass())
                    .hasMessage(e.getMessage());
        }
    }
}