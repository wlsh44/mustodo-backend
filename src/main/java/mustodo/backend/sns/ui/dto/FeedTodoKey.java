package mustodo.backend.sns.ui.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import mustodo.backend.sns.application.dto.TodoFeedQueryDto;


@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class FeedTodoKey {
    private Long userId;
    private String userName;
}
