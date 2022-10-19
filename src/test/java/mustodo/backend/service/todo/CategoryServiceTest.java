package mustodo.backend.service.todo;

import mustodo.backend.exception.todo.CategoryNotFoundException;
import mustodo.backend.todo.application.CategoryService;
import mustodo.backend.todo.domain.Category;
import mustodo.backend.todo.ui.dto.NewCategoryDto;
import mustodo.backend.auth.domain.User;
import mustodo.backend.todo.domain.CategoryRepository;
import mustodo.backend.todo.ui.dto.UpdateCategoryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    CategoryService categoryService;

    @Mock
    CategoryRepository categoryRepository;

    User user;

    Category category;

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

    @Nested
    @DisplayName("카테고리 수정 테스트")
    class UpdateTest {

        UpdateCategoryDto dto;

        Long categoryId;
        @BeforeEach
        void init() {
            categoryId = 1L;
            category = Category.builder()
                    .name("test")
                    .color("FFFFFF")
                    .publicAccess(false)
                    .user(user)
                    .build();
            dto = UpdateCategoryDto.builder()
                    .name("newName")
                    .color("000000")
                    .publicAccess(true)
                    .build();
        }

        @Test
        @DisplayName("수정 성공")
        void updateSuccess() {
            //given
            given(categoryRepository.findByIdAndUser(categoryId, user))
                    .willReturn(Optional.of(category));

            //when
            categoryService.update(user, dto, categoryId);

            //then
            assertThat(category.getName()).isEqualTo("newName");
            assertThat(category.getColor()).isEqualTo("000000");
            assertThat(category.isPublicAccess()).isTrue();
        }

        @Test
        @DisplayName("수정 실패 - 없는 카테고리")
        void updateFail_categoryNotFound() {
            //given
            CategoryNotFoundException e = new CategoryNotFoundException(categoryId);
            given(categoryRepository.findByIdAndUser(categoryId, user))
                    .willReturn(Optional.empty());

            //when then
            assertThatThrownBy(() -> categoryService.update(user, dto, categoryId))
                    .isInstanceOf(e.getClass())
                    .hasMessage(e.getMessage());
            assertThat(category.getName()).isEqualTo("test");
            assertThat(category.getColor()).isEqualTo("FFFFFF");
            assertThat(category.isPublicAccess()).isFalse();
        }
    }
}