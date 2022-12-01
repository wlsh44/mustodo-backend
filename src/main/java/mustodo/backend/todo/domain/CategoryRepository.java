package mustodo.backend.todo.domain;

import mustodo.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
    Optional<Category> findByUserAndId(User user, Long categoryId);
    List<Category> findAllByUser(User user);
    boolean existsByUserAndId(User user, Long categoryIdz);
}
