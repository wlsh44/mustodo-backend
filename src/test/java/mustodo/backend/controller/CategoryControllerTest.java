package mustodo.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mustodo.backend.auth.domain.User;
import mustodo.backend.auth.domain.embedded.EmailAuth;
import mustodo.backend.exception.advice.dto.ErrorResponse;
import mustodo.backend.exception.auth.NotAuthorizedException;
import mustodo.backend.todo.application.CategoryService;
import mustodo.backend.todo.ui.CategoryController;
import mustodo.backend.todo.ui.dto.NewCategoryDto;
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

import static mustodo.backend.auth.ui.AuthController.LOGIN_SESSION_ID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @MockBean
    CategoryService categoryService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;

    MockHttpSession session;

    User user;

    String uri;

    @Nested
    @DisplayName("로그인 안 된 유저")
    class NotLoginTest {

        @Test
        @DisplayName("카테고리 생성")
        void saveTest_notLogin() throws Exception {
            //given
            NotAuthorizedException e = new NotAuthorizedException();
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());

            //when then
            mockMvc.perform(post("/api/category"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().json(mapper.writeValueAsString(errorResponse)));
        }


    }

    @Nested
    @DisplayName("권한 없는 유저")
    class UnAuthorizedUserTest {

        @BeforeEach
        void init() {
            session = new MockHttpSession();
            user = User.builder()
                    .id(1L)
                    .email("test@test.test")
                    .name("test")
                    .emailAuth(new EmailAuth("123123", false))
                    .password("test")
                    .build();
        }

        @Test
        @DisplayName("권한 없는 유저")
        void saveTest_unAuthorizedUser() throws Exception {
            NotAuthorizedException e = new NotAuthorizedException();
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());

            //when then
            mockMvc.perform(post("/api/category"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().json(mapper.writeValueAsString(errorResponse)));
        }
    }

    @Nested
    @DisplayName("카테고리 생성 테스트")
    class SaveTest {

        NewCategoryDto dto;

        @BeforeEach
        void init() {
            uri = "/api/category";
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

        @Test
        @DisplayName("생성 성공")
        void saveSuccess() throws Exception {
            //given
            dto = NewCategoryDto.builder()
                    .name("test")
                    .color("FFFFFF")
                    .build();

            //when then
            mockMvc.perform(post(uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(session)
                            .content(mapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(""));
        }
    }
}