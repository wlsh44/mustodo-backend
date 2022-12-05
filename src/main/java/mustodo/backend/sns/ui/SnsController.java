package mustodo.backend.sns.ui;

import lombok.RequiredArgsConstructor;
import mustodo.backend.auth.ui.resolver.Login;
import mustodo.backend.sns.application.SnsService;
import mustodo.backend.sns.ui.dto.FeedTodoResponse;
import mustodo.backend.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/sns")
@RequiredArgsConstructor
public class SnsController {

    private final SnsService snsService;

    @PostMapping("/follow/{followingId}")
    public void follow(@Login User user, @PathVariable Long followingId) {
        snsService.follow(user, followingId);
    }

    @PostMapping("/unfollow/{followingId}")
    public void unfollow(@Login User user, @PathVariable Long followingId) {
        snsService.unfollow(user, followingId);
    }

    @GetMapping("/todo")
    public ResponseEntity<FeedTodoResponse> findTodoFeed(@Login User user, Pageable pageable) {
        FeedTodoResponse response = snsService.findTodoFeed(user, pageable);
        return ResponseEntity.ok(response);
    }
}
