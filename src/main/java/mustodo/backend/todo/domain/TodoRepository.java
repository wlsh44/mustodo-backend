package mustodo.backend.todo.domain;

import mustodo.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findAllByCategory_Id(Long categoryId);

    List<Todo> findAllByUserAndDate(User user, LocalDate date);
    Optional<Todo> findByUserAndId(User user, Long todoId);
}
