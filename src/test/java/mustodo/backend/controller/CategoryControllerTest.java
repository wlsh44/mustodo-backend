package mustodo.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mustodo.backend.auth.domain.User;
import mustodo.backend.auth.domain.embedded.EmailAuth;
import mustodo.backend.exception.advice.dto.ErrorResponse;
import mustodo.backend.exception.auth.NotAuthorizedException;
import mustodo.backend.exception.todo.CategoryNotFoundException;
import mustodo.backend.todo.application.CategoryService;
import mustodo.backend.todo.domain.Category;
import mustodo.backend.todo.ui.CategoryController;
import mustodo.backend.todo.ui.dto.NewCategoryDto;
import mustodo.backend.todo.ui.dto.UpdateCategoryDto;
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


import static mustodo.backend.auth.ui.AuthController.LOGIN_SESSION_ID;
import static mustodo.backend.exception.enums.BasicErrorCode.INVALID_ARGUMENT_ERROR;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @MockBean
    CategoryService categoryService;

    @Autowired
    ObjectMapper mapper;

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    MockHttpSession session;

    User user;

    Category category;

    String uri;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

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

        @Test
        @DisplayName("카테고리 수정")
        void updateTest_notLogin() throws Exception {
            //given
            NotAuthorizedException e = new NotAuthorizedException();
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());

            //when then
            mockMvc.perform(put("/api/category/1"))
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
        @DisplayName("카테고리 생성")
        void saveTest_unAuthorizedUser() throws Exception {
            NotAuthorizedException e = new NotAuthorizedException();
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());

            //when then
            mockMvc.perform(post("/api/category"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().json(mapper.writeValueAsString(errorResponse)));
        }

        @Test
        @DisplayName("카테고리 수정")
        void updateTest_unAuthorizedUser() throws Exception {
            NotAuthorizedException e = new NotAuthorizedException();
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());

            //when then
            mockMvc.perform(put("/api/category/1"))
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
                    .color("#FFFFFF")
                    .build();

            //when then
            mockMvc.perform(post(uri)
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(mapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(""));
        }

        @Test
        @DisplayName("카테고리 수정 실패 - 잘못된 dto")
        void updateFail_invalidDto() throws Exception {
            //given
            ErrorResponse response = new ErrorResponse(INVALID_ARGUMENT_ERROR);
            dto = NewCategoryDto.builder()
                    .name("newName")
                    .color("#0000000")
                    .build();

            mockMvc.perform(post(uri)
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(mapper.writeValueAsString(response)));
        }
    }

    @Nested
    @DisplayName("카테고리 수정 테스트")
    class UpdateTest {

        UpdateCategoryDto dto;

        @BeforeEach
        void init() {
            uri = "/api/category/{categoryId}";
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
        @DisplayName("카테고리 수정 성공")
        void updateSuccess() throws Exception {
            //given
            dto = UpdateCategoryDto.builder()
                    .name("newName")
                    .color("#000000")
                    .publicAccess(true)
                    .build();

            mockMvc.perform(put(uri, 1)
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(""));
        }

        @Test
        @DisplayName("카테고리 수정 실패 - 잘못된 dto")
        void updateFail_invalidDto() throws Exception {
            //given
            ErrorResponse response = new ErrorResponse(INVALID_ARGUMENT_ERROR);
            dto = UpdateCategoryDto.builder()
                    .name("newName")
                    .color("#00000")
                    .publicAccess(true)
                    .build();

            mockMvc.perform(put(uri, 1)
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(mapper.writeValueAsString(response)));
        }

        @Test
        @DisplayName("카테고리 수정 실패 - 없는 카테고리")
        void updateFail_categoryNotFound() throws Exception {
            //given
            CategoryNotFoundException e = new CategoryNotFoundException(1L);
            ErrorResponse response = new ErrorResponse(e.getErrorCode());
            dto = UpdateCategoryDto.builder()
                    .name("newName")
                    .color("#000000")
                    .publicAccess(true)
                    .build();
            doThrow(e).when(categoryService).update(user, 1L, dto);

            mockMvc.perform(put(uri, 1)
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(mapper.writeValueAsString(response)));
        }
    }
}