package mustodo.backend.todo.ui.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RepeatMeta {

    @NotNull
    @NotEmpty
    private List<DayOfWeek> repeatDay;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;
}
