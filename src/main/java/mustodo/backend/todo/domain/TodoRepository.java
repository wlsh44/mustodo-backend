package mustodo.backend.todo.domain;

import mustodo.backend.sns.application.dto.FeedTodoQueryDto;
import mustodo.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findAllByCategory_Id(Long categoryId);

    @Query("select todo from Todo todo where (todo.user=:user and todo.date=:date) or (todo.dDay=true)")
    List<Todo> findAllByUserAndDate(User user, LocalDate date);
    Optional<Todo> findByUserAndId(User user, Long todoId);

    @Query("select todo.content as todoContent, todo.category.color as categoryColor, todo.user.id as userId, todo.user.name as userName, todo.user.profile as profile " +
            "from Todo todo " +
            "where todo.user in :users " +
            "and todo.date=:now " +
            "and todo.achieve=true")
    List<FeedTodoQueryDto> findByUserInAndDateAndAchieveTrue(List<User> users, LocalDate now);
}
