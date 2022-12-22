package mustodo.backend.user.ui.dto;

import java.time.LocalDate;

public interface StatsMonthDto {
    LocalDate getDate();
    int getAchieveCount();
    int getTotalTodo();
}
