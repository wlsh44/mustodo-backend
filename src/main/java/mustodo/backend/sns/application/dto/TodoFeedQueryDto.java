package mustodo.backend.sns.application.dto;

public interface TodoFeedQueryDto {
    Long getUserId();
    String getUserName();
    String getTodoContent();
    String getCategoryColor();
}