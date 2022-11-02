package mustodo.backend.todo.application;

import mustodo.backend.exception.advice.dto.ErrorResponse;
import mustodo.backend.exception.todo.CategoryNotFoundException;
import mustodo.backend.todo.application.CategoryService;
import mustodo.backend.todo.domain.Category;
import mustodo.backend.todo.ui.dto.CategoryResponse;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        category = Category.builder()
                .id(1L)
                .name("test")
                .color("#FFFFFF")
                .user(user)
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
                    .color("#FFFFFF")
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
            dto = UpdateCategoryDto.builder()
                    .name("newName")
                    .color("#000000")
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
            categoryService.update(user, categoryId, dto);

            //then
            assertThat(category.getName()).isEqualTo("newName");
            assertThat(category.getColor()).isEqualTo("#000000");
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
            assertThatThrownBy(() -> categoryService.update(user, categoryId, dto))
                    .isInstanceOf(e.getClass())
                    .hasMessage(e.getMessage());
            assertThat(category.getName()).isEqualTo("test");
            assertThat(category.getColor()).isEqualTo("#FFFFFF");
            assertThat(category.isPublicAccess()).isFalse();
        }
    }

    @Nested
    @DisplayName("카테고리 조회 테스트")
    class FindTest {

        Long categoryId;

        @BeforeEach
        void init() {
            categoryId = 1L;
        }

        @Test
        @DisplayName("조회 성공")
        void findSuccess() {
            //given
            CategoryResponse expect = CategoryResponse.from(category);
            given(categoryRepository.findByIdAndUser(categoryId, user))
                    .willReturn(Optional.of(category));

            //when
            CategoryResponse categoryResponse = categoryService.find(user, categoryId);

            assertThat(categoryResponse).isEqualTo(expect);
        }

        @Test
        @DisplayName("조회 실패 - 없는 카테고리")
        void findFail_notExistCategory() {
            //given
            CategoryNotFoundException e = new CategoryNotFoundException(categoryId);
            given(categoryRepository.findByIdAndUser(categoryId, user))
                    .willReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.find(user, categoryId))
                    .isInstanceOf(e.getClass())
                    .hasMessage(e.getMessage());
        }
    }

    @Nested
    @DisplayName("카테고리 전체 조회 테스트")
    class FindAll {

        @Test
        @DisplayName("전체 조회 성공")
        void findAllSuccess() {
            //given
            Category category1 = Category.builder().id(1L).build();
            Category category2 = Category.builder().id(2L).build();
            List<Category> categoryList = List.of(category1, category2);
            List<CategoryResponse> expect = categoryList.stream().map(CategoryResponse::from)
                    .collect(Collectors.toList());
            given(categoryRepository.findAllByUser(user)).willReturn(categoryList);

            //when
            List<CategoryResponse> responseList = categoryService.findAll(user);

            //then
            assertThat(responseList).isEqualTo(expect);
        }
    }

    @Nested
    @DisplayName("카테고리 삭제 테스트")
    class DeleteTest {

        Long categoryId;

        @BeforeEach
        void init() {
            categoryId = 1L;
        }

        @Test
        @DisplayName("삭제 성공")
        void deleteSuccess() {
            //given
            given(categoryRepository.findByIdAndUser(categoryId, user))
                    .willReturn(Optional.of(category));

            //when
            categoryService.delete(user, categoryId);
        }

        @Test
        @DisplayName("삭제 실패 - 없는 카테고리")
        void deleteFail_notExistCategory() {
            //given
            CategoryNotFoundException e = new CategoryNotFoundException(categoryId);
            given(categoryRepository.findByIdAndUser(categoryId, user))
                    .willReturn(Optional.empty());

            //when
            assertThatThrownBy(() -> categoryService.delete(user, categoryId))
                    .isInstanceOf(e.getClass())
                    .hasMessage(e.getMessage());
        }
    }

}