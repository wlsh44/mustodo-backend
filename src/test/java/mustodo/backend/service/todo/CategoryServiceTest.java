package mustodo.backend.service.todo;

import mustodo.backend.todo.application.CategoryService;
import mustodo.backend.todo.domain.Category;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

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

        Category category;

        @BeforeEach
        void init() {
            dto = NewCategoryDto.builder()
                    .name("test")
                    .color("FFFFFF")
                    .build();
            category = Category.builder()
                    .id(1L)
                    .name("test")
                    .color("FFFFFF")
                    .user(user)
                    .build();
        }

        @Test
        @DisplayName("카테고리 생성 성공")
        void saveSuccessTest() {
            //given
            given(categoryRepository.save(any()))
                    .willReturn(category);

            //when
            Long saveId = categoryService.save(user, dto);

            //then
            assertThat(saveId).isEqualTo(1L);
        }
    }
}