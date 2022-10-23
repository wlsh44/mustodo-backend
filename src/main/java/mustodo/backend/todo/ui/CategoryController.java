package mustodo.backend.todo.ui;

import lombok.RequiredArgsConstructor;
import mustodo.backend.todo.ui.dto.NewCategoryDto;
import mustodo.backend.auth.domain.User;
import mustodo.backend.todo.application.CategoryService;
import mustodo.backend.auth.ui.resolver.Login;
import mustodo.backend.todo.ui.dto.UpdateCategoryDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.OK)
    public void save(@Login User user, @RequestBody @Valid NewCategoryDto dto) {
        categoryService.save(user, dto);
    }

    @PutMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@Login User user, @PathVariable Long categoryId, @RequestBody @Valid UpdateCategoryDto dto) {
        categoryService.update(user, categoryId, dto);
    }
}
