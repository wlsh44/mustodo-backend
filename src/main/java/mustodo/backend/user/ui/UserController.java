package mustodo.backend.user.ui;

import lombok.RequiredArgsConstructor;
import mustodo.backend.auth.ui.resolver.Login;
import mustodo.backend.user.application.UserService;
import mustodo.backend.user.domain.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/me")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/profile")
    public void updateProfileImage(@Login User user, @RequestBody MultipartFile image) {
        userService.updateProfileImage(user, image);
    }
}
