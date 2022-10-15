package mustodo.backend.service.todo;

import lombok.RequiredArgsConstructor;
import mustodo.backend.dto.MessageDto;
import mustodo.backend.dto.category.NewCategoryDto;
import mustodo.backend.entity.Category;
import mustodo.backend.entity.User;
import mustodo.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static mustodo.backend.enums.response.CategoryResponseMsg.CREATE_CATEGORY_FAILED;
import static mustodo.backend.enums.response.CategoryResponseMsg.CREATE_CATEGORY_SUCCESS;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public MessageDto save(User user, NewCategoryDto dto) {
        Category category = toCategoryEntity(user, dto);
        categoryRepository.save(category);

        return MessageDto.builder()
                .message(CREATE_CATEGORY_SUCCESS)
                .build();
    }

    private Category toCategoryEntity(User user, NewCategoryDto dto) {
        return Category.builder()
                .name(dto.getName())
                .color(dto.getColor())
                .user(user)
                .build();
    }
}
