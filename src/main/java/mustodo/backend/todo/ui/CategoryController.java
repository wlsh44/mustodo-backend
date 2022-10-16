package mustodo.backend.todo.ui;

import lombok.RequiredArgsConstructor;
import mustodo.backend.dto.MessageDto;
import mustodo.backend.todo.ui.dto.NewCategoryDto;
import mustodo.backend.auth.domain.User;
import mustodo.backend.todo.application.CategoryService;
import mustodo.backend.auth.application.resolver.Login;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("")
    public ResponseEntity<MessageDto> save(@Login User user, NewCategoryDto dto) {
        MessageDto messageDto = categoryService.save(user, dto);

        return ResponseEntity.ok(messageDto);
    }
}
