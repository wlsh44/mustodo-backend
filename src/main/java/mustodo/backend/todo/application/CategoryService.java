package mustodo.backend.todo.application;

import lombok.RequiredArgsConstructor;
import mustodo.backend.exception.todo.CategoryNotFoundException;
import mustodo.backend.todo.ui.dto.NewCategoryDto;
import mustodo.backend.todo.domain.Category;
import mustodo.backend.auth.domain.User;
import mustodo.backend.todo.domain.CategoryRepository;
import mustodo.backend.todo.ui.dto.UpdateCategoryDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public Long save(User user, NewCategoryDto dto) {
        Category category = toCategoryEntity(user, dto);
        Category saveCategory = categoryRepository.save(category);

        return saveCategory.getId();
    }

    private Category toCategoryEntity(User user, NewCategoryDto dto) {
        return Category.builder()
                .name(dto.getName())
                .color(dto.getColor())
                .user(user)
                .publicAccess(false)
                .build();
    }

    @Transactional
    public void update(User user, Long categoryId, UpdateCategoryDto dto) {
        Category category = categoryRepository.findByIdAndUser(categoryId, user)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        category.update(dto);
    }
}
