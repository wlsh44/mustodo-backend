package mustodo.backend.todo.ui;

import lombok.RequiredArgsConstructor;
import mustodo.backend.todo.ui.dto.CategoryResponse;
import mustodo.backend.todo.ui.dto.NewCategoryDto;
import mustodo.backend.auth.domain.User;
import mustodo.backend.todo.application.CategoryService;
import mustodo.backend.auth.ui.resolver.Login;
import mustodo.backend.todo.ui.dto.UpdateCategoryDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

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

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> find(@Login User user, @PathVariable Long categoryId) {
        CategoryResponse categoryResponse = categoryService.find(user, categoryId);
        return ResponseEntity.ok(categoryResponse);
    }

    @GetMapping("")
    public ResponseEntity<List<CategoryResponse>> findAll(@Login User user) {
        List<CategoryResponse> categoryResponseList = categoryService.findAll(user);
        return ResponseEntity.ok(categoryResponseList);
    }

    @DeleteMapping("/{categoryId}")
    public void delete(@Login User user, @PathVariable Long categoryId) {
        categoryService.delete(user, categoryId);
    }
}
