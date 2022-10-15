package mustodo.backend.controller;

import lombok.RequiredArgsConstructor;
import mustodo.backend.dto.MessageDto;
import mustodo.backend.dto.category.NewCategoryDto;
import mustodo.backend.entity.User;
import mustodo.backend.service.auth.resolver.Login;
import mustodo.backend.service.todo.CategoryService;
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
