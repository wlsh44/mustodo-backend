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
import mustodo.backend.todo.ui.dto.CategoryResponse;
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


import java.util.List;
import java.util.stream.Collectors;

import static mustodo.backend.auth.ui.AuthController.LOGIN_SESSION_ID;
import static mustodo.backend.exception.enums.BasicErrorCode.INVALID_ARGUMENT_ERROR;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    @DisplayName("로그인 안 된 유저")
    class NotLoginTest {


        NotAuthorizedException e;

        ErrorResponse errorResponse;

        @BeforeEach
        void init() {
            session = new MockHttpSession();
            e = new NotAuthorizedException();
            errorResponse = new ErrorResponse(e.getErrorCode());
        }

        @Test
        @DisplayName("카테고리 생성")
        void save() throws Exception {
            //when then
            mockMvc.perform(post("/api/category"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().json(mapper.writeValueAsString(errorResponse)));
        }

        @Test
        @DisplayName("카테고리 수정")
        void update() throws Exception {
            //when then
            mockMvc.perform(put("/api/category/1"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().json(mapper.writeValueAsString(errorResponse)));
        }

        @Test
        @DisplayName("카테고리 하나 조회")
        void find() throws Exception {
            //when then
            mockMvc.perform(get("/api/category/1"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().json(mapper.writeValueAsString(errorResponse)));
        }

        @Test
        @DisplayName("카테고리 전체 조회")
        void findAll() throws Exception {
            //when then
            mockMvc.perform(get("/api/category"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().json(mapper.writeValueAsString(errorResponse)));
        }
    }

    @Nested
    @DisplayName("권한 없는 유저")
    class UnAuthorizedUserTest {

        NotAuthorizedException e;
        ErrorResponse errorResponse;

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
            e = new NotAuthorizedException();
            errorResponse = new ErrorResponse(e.getErrorCode());
        }

        @Test
        @DisplayName("카테고리 생성")
        void save() throws Exception {
            //when then
            mockMvc.perform(post("/api/category"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().json(mapper.writeValueAsString(errorResponse)));
        }

        @Test
        @DisplayName("카테고리 수정")
        void update() throws Exception {
            //when then
            mockMvc.perform(put("/api/category/1"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().json(mapper.writeValueAsString(errorResponse)));
        }

        @Test
        @DisplayName("카테고리 하나 조회")
        void find() throws Exception {
            //when then
            mockMvc.perform(get("/api/category/1"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().json(mapper.writeValueAsString(errorResponse)));
        }

        @Test
        @DisplayName("카테고리 전체 조회")
        void findAll() throws Exception {
            //when then
            mockMvc.perform(get("/api/category"))
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

            //when then
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

            //when then
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

            //when then
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

            //when then
            mockMvc.perform(put(uri, 1)
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(mapper.writeValueAsString(response)));
        }
    }

    @Nested
    @DisplayName("카테고리 하나 조회")
    class FindTest {

        Long categoryId;

        @BeforeEach
        void init() {
            uri = "/api/category/{categoryId}";
            categoryId = 1L;
            category = Category.builder()
                    .id(1L)
                    .name("test")
                    .publicAccess(false)
                    .color("#FFFFFF")
                    .user(user)
                    .build();
        }

        @Test
        @DisplayName("조회 성공")
        void findSuccess() throws Exception {
            //given
            CategoryResponse expect = CategoryResponse.from(category);
            given(categoryService.find(user, categoryId))
                    .willReturn(CategoryResponse.from(category));

            //when then
            mockMvc.perform(get(uri, categoryId)
                            .session(session))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(expect)));
        }

        @Test
        @DisplayName("카테고리 조회 실패 - 없는 카테고리")
        void updateFail_categoryNotFound() throws Exception {
            //given
            CategoryNotFoundException e = new CategoryNotFoundException(1L);
            ErrorResponse response = new ErrorResponse(e.getErrorCode());
            given(categoryService.find(user, categoryId))
                    .willThrow(e);

            //when then
            mockMvc.perform(get(uri, categoryId)
                            .session(session))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(mapper.writeValueAsString(response)));
        }
    }

    @Nested
    @DisplayName("카테고리 전체 조회")
    class FindAllTest {

        Category category1;
        Category category2;

        List<Category> categoryList;
        @BeforeEach

        void init() {
            uri = "/api/category";
            category1 = Category.builder()
                    .id(1L)
                    .name("test")
                    .publicAccess(false)
                    .color("#FFFFFF")
                    .user(user)
                    .build();
            category2 = Category.builder()
                    .id(2L)
                    .name("test2")
                    .publicAccess(false)
                    .color("#000000")
                    .user(user)
                    .build();
            categoryList = List.of(category1, category2);
        }

        @Test
        @DisplayName("카테고리 전체 조회 성공")
        void findAllSuccess() throws Exception {
            //given
            List<CategoryResponse> expect = categoryList.stream()
                    .map(CategoryResponse::from)
                    .collect(Collectors.toList());
            given(categoryService.findAll(user)).willReturn(expect);

            //when then
            mockMvc.perform(get(uri)
                            .session(session))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(expect)));
        }
    }
}