package mustodo.backend.sns.ui.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FeedTodoResponse {
    private Long userId;
    private String userName;
    private String profilePath;
    private String biography;
    private List<FeedTodoValue> todo;
}
