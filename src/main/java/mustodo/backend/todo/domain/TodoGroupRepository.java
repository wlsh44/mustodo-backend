package mustodo.backend.todo.domain;

import mustodo.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TodoGroupRepository extends JpaRepository<TodoGroup, Long> {
    Optional<TodoGroup> findByUserAndId(User user, Long groupId);
}
