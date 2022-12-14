package mustodo.backend.todo.ui.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TodoMonthResponse {
    private final String date;

    public TodoMonthResponse(LocalDate date) {
        this.date = date.toString();
    }
}
