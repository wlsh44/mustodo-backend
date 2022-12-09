package mustodo.backend.sns.application.dto;

import mustodo.backend.user.domain.embedded.Image;

public interface TodoFeedQueryDto {
    Long getUserId();
    String getUserName();
    Image getProfile();
    String getTodoContent();
    String getCategoryColor();
}