package mustodo.backend.todo.domain;

import mustodo.backend.sns.application.dto.FeedTodoQueryDto;
import mustodo.backend.user.domain.User;
import mustodo.backend.user.ui.dto.StatsMonthDto;
import mustodo.backend.user.ui.dto.StatsResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findAllByCategory_Id(Long categoryId);

    @Query("select todo from Todo todo where todo.user=:user and ((todo.date=:date) or (todo.dDay=true and :date <= todo.date and todo.achieve = false))")
    List<Todo> findAllByUserAndDate(User user, LocalDate date);
    Optional<Todo> findByUserAndId(User user, Long todoId);

    @Query("select todo.content as todoContent, todo.category.color as categoryColor, todo.user.id as userId, " +
            "todo.user.name as userName, todo.user.profile as profile, todo.user.biography as biography " +
            "from Todo todo " +
            "where todo.date=:now " +
            "and todo.achieve=true and todo.isPublic=true")
    List<FeedTodoQueryDto> findByTodayAchieveTrue(LocalDate now);

    @Query("select todo from Todo todo where todo.user=:user and :monthStart <= todo.date and todo.date <= :monthEnd")
    List<Todo> findTodoByMonth(User user, LocalDate monthStart, LocalDate monthEnd);

    @Query("select todo.date as date, sum(case when todo.achieve = true then 1 else 0 end) as achieveCount, count(todo) as totalTodo from Todo todo " +
            "where todo.user=:user and :monthStart <= todo.date and todo.date <= :monthEnd group by todo.date")
    List<StatsMonthDto> findTodoStatsByMonth(User user, LocalDate monthStart, LocalDate monthEnd);
}
