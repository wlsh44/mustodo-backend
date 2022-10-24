package mustodo.backend.todo.domain;

import mustodo.backend.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
    Optional<Category> findByIdAndUser(Long categoryId, User user);
    List<Category> findAllByUser(User user);
}
