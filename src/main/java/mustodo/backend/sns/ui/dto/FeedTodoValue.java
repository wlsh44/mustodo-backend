package mustodo.backend.sns.ui.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class FeedTodoValue {
    private String content;
    private String color;
}
