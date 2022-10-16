package mustodo.backend.service.todo;

import mustodo.backend.dto.MessageDto;
import mustodo.backend.todo.application.CategoryService;
import mustodo.backend.todo.ui.dto.NewCategoryDto;
import mustodo.backend.auth.domain.User;
import mustodo.backend.todo.domain.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static mustodo.backend.enums.response.CategoryResponseMsg.CREATE_CATEGORY_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    CategoryService categoryService;

    @Mock
    CategoryRepository categoryRepository;

    User user;

    @BeforeEach
    void init() {
        user = User.builder()
                .build();
    }

    @Nested
    @DisplayName("카테고리 생성 태스트")
    class SaveTest {

        NewCategoryDto dto;

        @BeforeEach
        void init() {
            dto = NewCategoryDto.builder()
                    .name("test")
                    .color("FFFFFF")
                    .build();
        }

        @Test
        @DisplayName("카테고리 생성 성공")
        void saveSuccessTest() {
            //given
            MessageDto messageDto = MessageDto.builder()
                    .message(CREATE_CATEGORY_SUCCESS)
                    .build();

            //when
            MessageDto res = categoryService.save(user, dto);

            //then
            assertThat(res).isEqualTo(messageDto);
        }
    }
}