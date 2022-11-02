package mustodo.backend.todo.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import mustodo.backend.exception.advice.dto.ErrorResponse;
import mustodo.backend.exception.todo.CategoryNotFoundException;
import mustodo.backend.exception.todo.InvalidRepeatRangeException;
import mustodo.backend.todo.application.TodoService;
import mustodo.backend.todo.domain.Category;
import mustodo.backend.todo.domain.Todo;
import mustodo.backend.todo.ui.dto.NewRepeatTodoDto;
import mustodo.backend.todo.ui.dto.NewTodoDto;
import mustodo.backend.todo.ui.dto.RepeatMeta;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    @DisplayName("할 일 생성 테스트")
    class SaveTodoTest {

        @BeforeEach
        void init() {
            uri = "/api/todo";
        }

        @Test
        @DisplayName("할 일 생성 성공")
        void saveTodoSuccess() throws Exception {
            //given
            NewTodoDto newTodoDto = new NewTodoDto(1L, "할 일", false, LocalDate.now(), LocalDateTime.now());

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
        @DisplayName("할 일 생성 실패 - 카테고리 없음")
        void saveTodoFail_categoryNotFound() throws Exception {
            //given
            long categoryId = 1L;
            CategoryNotFoundException e = new CategoryNotFoundException(categoryId);
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
            NewTodoDto newTodoDto = new NewTodoDto(categoryId, "할 일", false, LocalDate.now(), LocalDateTime.now());
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
    @DisplayName("반복 할 일 생성 테스트")
    class SaveRepeatTodoTest {

        @BeforeEach
        void init() {
            uri = "/api/todo/repeat";
        }

        @Test
        @DisplayName("할 일 생성 성공")
        void saveTodoSuccess() throws Exception {
            //given
            NewRepeatTodoDto newTodoDto = new NewRepeatTodoDto(1L, "할 일", false, LocalDateTime.now(),
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
        @DisplayName("할 일 생성 실패 - 카테고리 없음")
        void saveTodoFail_categoryNotFound() throws Exception {
            //given
            long categoryId = 1L;
            CategoryNotFoundException e = new CategoryNotFoundException(categoryId);
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
            NewRepeatTodoDto newTodoDto = new NewRepeatTodoDto(categoryId, "할 일", false, LocalDateTime.now(),
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
        @DisplayName("할 일 생성 실패 - 카테고리 없음")
        void saveTodoFail_invalidRepeatRange() throws Exception {
            //given
            InvalidRepeatRangeException e = new InvalidRepeatRangeException();
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
            NewRepeatTodoDto newTodoDto = new NewRepeatTodoDto(1L, "할 일", false, LocalDateTime.now(),
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
}