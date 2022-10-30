package mustodo.backend.todo.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mustodo.backend.auth.domain.User;
import mustodo.backend.todo.ui.dto.NewTodoDto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(name = "is_achieved", nullable = false)
    private boolean achieve;

    @Column(name = "is_dday", nullable = false)
    private boolean dDay;

    @Column(nullable = false)
    private LocalDateTime alarm;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "todo_group_id")
    private TodoGroup todoGroup;

    public Todo(NewTodoDto dto, Category category, User user, TodoGroup todoGroup, LocalDate date) {
        this.content = dto.getContent();
        this.achieve = false;
        this.dDay = dto.isDDay();
        this.alarm = dto.getAlarm();
        this.date = date;
        this.category = category;
        this.user = user;
        this.todoGroup = todoGroup;
    }

    public Todo(NewTodoDto dto, Category category, User user) {
        this(dto, category, user, null, dto.getDate());
    }
}
