package mustodo.backend.user.ui.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StatsResponse {
    private List<StatsMonthDto> month;
}
