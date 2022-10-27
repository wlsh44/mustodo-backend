package mustodo.backend.todo.ui.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NewTodoDto {

    @NotNull
    private Long categoryId;

    @NotBlank
    private String content;

    private boolean dDay;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate date;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime alarm;

    private TodoRepeatDto todoRepeat;
}

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class TodoRepeatDto {

    @NotNull
    @NotEmpty
    private List<DayOfWeek> repeatDay;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;
}