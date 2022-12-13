package mustodo.backend.sns.application.dto;

import mustodo.backend.user.domain.embedded.Image;

public interface FeedTodoQueryDto {
    Long getUserId();
    String getUserName();
    Image getProfile();
    String getTodoContent();
    String getCategoryColor();
}