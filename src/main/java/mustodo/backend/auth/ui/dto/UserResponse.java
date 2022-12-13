package mustodo.backend.auth.ui.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mustodo.backend.user.domain.User;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String profile;
    private String biography;

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getProfile().getFileUrl() + user.getProfile().getFileName(), user.getBiography());
    }
}
