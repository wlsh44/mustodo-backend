package mustodo.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mustodo.backend.auth.domain.User;
import mustodo.backend.exception.dto.ErrorResponse;
import mustodo.backend.exception.auth.NotAuthorizedException;
import mustodo.backend.todo.application.CategoryService;
import mustodo.backend.todo.ui.CategoryController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

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

    @Nested
    @DisplayName("로그인 안 된 테스트")
    class NotLoginTest {

        @Test
        void saveTest() throws Exception {
            NotAuthorizedException e = new NotAuthorizedException();
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
            //when then
            mockMvc.perform(post("/api/category"))
                    .andDo(print())
                    .andExpect(content().json(mapper.writeValueAsString(errorResponse)));
        }
    }
}