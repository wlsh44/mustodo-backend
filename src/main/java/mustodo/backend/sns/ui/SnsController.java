package mustodo.backend.sns.ui;

import lombok.RequiredArgsConstructor;
import mustodo.backend.auth.ui.resolver.Login;
import mustodo.backend.sns.application.SnsService;
import mustodo.backend.user.domain.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sns")
@RequiredArgsConstructor
public class SnsController {

    private final SnsService snsService;

    @PostMapping("/follow/{followerId}")
    public void follow(@Login User user, @PathVariable Long followerId) {
        snsService.follow(user, followerId);
    }
}
