package mustodo.backend.todo.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import mustodo.backend.exception.advice.dto.ErrorResponse;
import mustodo.backend.exception.todo.CategoryNotFoundException;
import mustodo.backend.exception.todo.InvalidDateFormatException;
import mustodo.backend.exception.todo.InvalidRepeatRangeException;
import mustodo.backend.exception.todo.TodoNotFoundException;
import mustodo.backend.todo.application.TodoService;
import mustodo.backend.todo.domain.Category;
import mustodo.backend.todo.domain.Todo;
import mustodo.backend.todo.ui.dto.NewRepeatTodoDto;
import mustodo.backend.todo.ui.dto.NewTodoDto;
import mustodo.backend.todo.ui.dto.RepeatMeta;
import mustodo.backend.todo.ui.dto.TodoByDateResponse;
import mustodo.backend.todo.ui.dto.TodoDetailResponse;
import mustodo.backend.todo.ui.dto.TodoResponse;
import mustodo.backend.todo.ui.dto.UpdateTodoDto;
import mustodo.backend.user.domain.User;
import mustodo.backend.user.domain.embedded.EmailAuth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static mustodo.backend.auth.ui.AuthController.LOGIN_SESSION_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = TodoController.class)
class TodoControllerTest {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    TodoService todoService;

    MockHttpSession session;

    User user;

    String uri;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
        user = User.builder()
                .id(1L)
                .email("test@test.test")
                .name("test")
                .emailAuth(new EmailAuth("123123", true))
                .password("test")
                .build();
        session = new MockHttpSession();
        session.setAttribute(LOGIN_SESSION_ID, user);
    }

    @Nested
    @DisplayName("??? ??? ?????? ?????????")
    class SaveTodoTest {

        @BeforeEach
        void init() {
            uri = "/api/todo";
        }

        @Test
        @DisplayName("??? ??? ?????? ??????")
        void saveTodoSuccess() throws Exception {
            //given
            NewTodoDto newTodoDto = new NewTodoDto(1L, "??? ???", false, LocalDate.now(), LocalDateTime.now());

            //when then
            mockMvc.perform(post(uri)
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(newTodoDto)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(""));
        }

        @Test
        @DisplayName("??? ??? ?????? ?????? - ???????????? ??????")
        void saveTodoFail_categoryNotFound() throws Exception {
            //given
            long categoryId = 1L;
            CategoryNotFoundException e = new CategoryNotFoundException(categoryId);
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
            NewTodoDto newTodoDto = new NewTodoDto(categoryId, "??? ???", false, LocalDate.now(), LocalDateTime.now());
            doThrow(e).when(todoService).saveTodo(any(), any());

            //when then
            mockMvc.perform(post(uri)
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(newTodoDto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(mapper.writeValueAsString(errorResponse)));
        }
    }

    @Nested
    @DisplayName("?????? ??? ??? ?????? ?????????")
    class SaveRepeatTodoTest {

        @BeforeEach
        void init() {
            uri = "/api/todo/repeat";
        }

        @Test
        @DisplayName("??? ??? ?????? ??????")
        void saveTodoSuccess() throws Exception {
            //given
            NewRepeatTodoDto newTodoDto = new NewRepeatTodoDto(1L, "??? ???", false, LocalDateTime.now(),
                    new RepeatMeta(List.of(DayOfWeek.MONDAY, DayOfWeek.SATURDAY), LocalDate.now(), LocalDate.now().plusDays(1)));

            //when then
            mockMvc.perform(post(uri)
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(newTodoDto)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(""));
        }

        @Test
        @DisplayName("??? ??? ?????? ?????? - ???????????? ??????")
        void saveTodoFail_categoryNotFound() throws Exception {
            //given
            long categoryId = 1L;
            CategoryNotFoundException e = new CategoryNotFoundException(categoryId);
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
            NewRepeatTodoDto newTodoDto = new NewRepeatTodoDto(categoryId, "??? ???", false, LocalDateTime.now(),
                    new RepeatMeta(List.of(DayOfWeek.MONDAY, DayOfWeek.SATURDAY), LocalDate.now(), LocalDate.now().plusDays(1)));
            doThrow(e).when(todoService).saveRepeatTodo(any(), any());

            //when then
            mockMvc.perform(post(uri)
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(newTodoDto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(mapper.writeValueAsString(errorResponse)));
        }

        @Test
        @DisplayName("??? ??? ?????? ?????? - ???????????? ??????")
        void saveTodoFail_invalidRepeatRange() throws Exception {
            //given
            InvalidRepeatRangeException e = new InvalidRepeatRangeException();
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
            NewRepeatTodoDto newTodoDto = new NewRepeatTodoDto(1L, "??? ???", false, LocalDateTime.now(),
                    new RepeatMeta(List.of(DayOfWeek.MONDAY, DayOfWeek.SATURDAY), LocalDate.now().plusDays(1), LocalDate.now()));
            doThrow(e).when(todoService).saveRepeatTodo(any(), any());

            //when then
            mockMvc.perform(post(uri)
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(newTodoDto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(mapper.writeValueAsString(errorResponse)));
        }
    }

    @Nested
    @DisplayName("???????????? ??? ??? ??? ?????? ?????????")
    class FindByCategoryTest {

        @BeforeEach
        void init() {
            uri = "/api/todo";
        }

        @Test
        @DisplayName("?????? ??????")
        void success() throws Exception {
            //given
            Long categoryId = 1L;
            List<TodoResponse> expect = List.of(
                    new TodoResponse(1L, "??? ???1", false),
                    new TodoResponse(2L, "??? ???2", true),
                    new TodoResponse(3L, "??? ???3", false)
            );
            given(todoService.findByCategory(user, categoryId)).willReturn(expect);

            //when then
            mockMvc.perform(get(uri)
                            .queryParam("categoryId", String.valueOf(categoryId))
                            .session(session))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(expect)));
        }

        @Test
        @DisplayName("?????? ?????? - ???????????? ??????")
        void fail_categoryNotFound() throws Exception {
            //given
            long categoryId = 2L;
            CategoryNotFoundException e = new CategoryNotFoundException(categoryId);
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
            given(todoService.findByCategory(user, categoryId)).willThrow(e);

            //when then
            mockMvc.perform(get(uri)
                            .queryParam("categoryId", String.valueOf(categoryId))
                            .session(session))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(mapper.writeValueAsString(errorResponse)));
        }
    }

    @Nested
    @DisplayName("?????? ??? ??? ??? ?????? ?????????")
    class FindByDateTest {

        Category category1;
        Category category2;

        @BeforeEach
        void init() {
            uri = "/api/todo/date/{date}";
            category1 = new Category(1L, "category1", "#FFFFFF", false, user);
            category2 = new Category(2L, "category2", "#000000", true, user);
        }

        @Test
        @DisplayName("?????? ??????")
        void success() throws Exception {
            //given
            String date = "2022-10-15";
            List<TodoByDateResponse> expect = TodoByDateResponse.from(List.of(
                    new Todo(1L, "??? ???1", false, false, LocalDateTime.now(), LocalDate.of(2022, 10, 15), category1, user, null),
                    new Todo(2L, "??? ???2", true, false, LocalDateTime.now(), LocalDate.of(2022, 10, 15), category1, user, null),
                    new Todo(3L, "??? ???3", true, true, LocalDateTime.now(), LocalDate.of(2022, 10, 15), category2, user, null),
                    new Todo(4L, "??? ???4", false, true, LocalDateTime.now(), LocalDate.of(2022, 10, 17), category1, user, null)));
            given(todoService.findByDate(user, date)).willReturn(expect);

            //when then
            mockMvc.perform(get(uri, date)
                            .session(session))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(expect)));
        }

        @Test
        @DisplayName("?????? ?????? - ????????? ?????? ??????")
        void fail_categoryNotFound() throws Exception {
            //given
            String wrongDateFormat = "asdfasdf";
            InvalidDateFormatException e = new InvalidDateFormatException();
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
            given(todoService.findByDate(user, wrongDateFormat)).willThrow(e);

            //when then
            mockMvc.perform(get(uri, wrongDateFormat)
                            .session(session))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(mapper.writeValueAsString(errorResponse)));
        }
    }

    @Nested
    @DisplayName("??? ??? ?????? ?????????")
    class DeleteTodoTest {

        @BeforeEach
        void init() {
            uri = "/api/todo/{todoId}";
        }

        @Test
        @DisplayName("?????? ??????")
        void success() throws Exception {
            //given
            Long todoId = 1L;

            //when then
            mockMvc.perform(delete(uri, todoId)
                            .session(session))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(""));
        }

        @Test
        @DisplayName("?????? ?????? - ??? ??? ??????")
        void fail_todoNotFound() throws Exception {
            //given
            Long todoId = 1L;
            TodoNotFoundException e = new TodoNotFoundException(todoId);
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
            doThrow(e).when(todoService).deleteTodo(user, todoId);

            //when then
            mockMvc.perform(delete(uri, todoId)
                            .session(session))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(mapper.writeValueAsString(errorResponse)));
        }
    }

    @Nested
    @DisplayName("??? ??? ?????? ?????????")
    class CheckAchieveTest {

        @BeforeEach
        void init() {
            uri = "/api/todo/{todoId}";
        }

        @Test
        @DisplayName("?????? ??????")
        void success() throws Exception {
            //given
            Long todoId = 1L;

            //when then
            mockMvc.perform(patch(uri, todoId)
                            .session(session))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(""));
        }

        @Test
        @DisplayName("?????? ?????? - ??? ??? ??????")
        void fail_todoNotFound() throws Exception {
            //given
            Long todoId = 1L;
            TodoNotFoundException e = new TodoNotFoundException(todoId);
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
            doThrow(e).when(todoService).checkAchieve(user, todoId);

            //when then
            mockMvc.perform(patch(uri, todoId)
                            .session(session))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(mapper.writeValueAsString(errorResponse)));
        }
    }

    @Nested
    @DisplayName("??? ??? ?????? ?????????")
    class FindByIdTest {

        @BeforeEach
        void init() {
            uri = "/api/todo/{todoId}";
        }

        @Test
        @DisplayName("?????? ??????")
        void success() throws Exception {
            //given
            Long todoId = 1L;
            TodoDetailResponse expect = new TodoDetailResponse(todoId, 1L, null, "??? ???1", false, false, LocalDateTime.now(), LocalDate.of(2022, 10, 15));
            given(todoService.findById(user, todoId)).willReturn(expect);

            //when then
            mockMvc.perform(get(uri, todoId)
                            .session(session))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(mapper.writeValueAsString(expect)));
        }

        @Test
        @DisplayName("?????? ?????? - ??? ??? ??????")
        void fail_todoNotFound() throws Exception {
            //given
            Long todoId = 1L;
            TodoNotFoundException e = new TodoNotFoundException(todoId);
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
            given(todoService.findById(user, todoId)).willThrow(e);

            //when then
            mockMvc.perform(get(uri, todoId)
                            .session(session))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(mapper.writeValueAsString(errorResponse)));
        }
    }

    @Nested
    @DisplayName("??? ??? ?????? ?????????")
    class UpdateTest {

        UpdateTodoDto dto;

        @BeforeEach
        void init() {
            uri = "/api/todo/{todoId}";
            dto = new UpdateTodoDto(1L, "newContent", true, LocalDateTime.now(), LocalDate.now());
        }

        @Test
        @DisplayName("?????? ??????")
        void success() throws Exception {
            //given
            Long todoId = 1L;

            //when then
            mockMvc.perform(put(uri, todoId)
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(""));
        }

        @Test
        @DisplayName("?????? ?????? - ??? ??? ??????")
        void fail_todoNotFound() throws Exception {
            //given
            Long todoId = 1L;
            TodoNotFoundException e = new TodoNotFoundException(todoId);
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
            doThrow(e).when(todoService).update(eq(user), eq(todoId), any());

            //when then
            mockMvc.perform(put(uri, todoId)
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(mapper.writeValueAsString(errorResponse)));
        }

        @Test
        @DisplayName("?????? ?????? - ???????????? ??????")
        void fail_categoryNotFound() throws Exception {
            //given
            Long todoId = 1L;
            CategoryNotFoundException e = new CategoryNotFoundException(dto.getCategoryId());
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
            doThrow(e).when(todoService).update(eq(user), eq(todoId), any(UpdateTodoDto.class));

            //when then
            mockMvc.perform(put(uri, todoId)
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(mapper.writeValueAsString(errorResponse)));
        }
    }
}