package mustodo.backend.sns.ui.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FeedTodoResponse {
    private List<FeedTodoDto> todoFeed;

    public static FeedTodoResponse from(FeedTodoMapDto map) {
        List<FeedTodoDto> todoFeed = map.toFeedTodoDtoList();
        return new FeedTodoResponse(todoFeed);
    }
}
