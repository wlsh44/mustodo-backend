package mustodo.backend.todo.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mustodo.backend.todo.ui.dto.UpdateTodoDto;
import mustodo.backend.user.domain.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@EqualsAndHashCode(of = "id")
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

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

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

    public void checkAchieve() {
        this.achieve = !this.achieve;
    }

    public void update(UpdateTodoDto dto, Category category) {
        this.content = dto.getContent();
        this.dDay = dto.isDDay();
        this.alarm = dto.getAlarm();
        this.date = dto.getDate();
        this.isPublic = dto.isAchieve();
        this.category = category;
    }
}
