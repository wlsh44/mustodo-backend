package mustodo.backend.todo.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoGroupRepository extends JpaRepository<TodoGroup, Long> {

}
