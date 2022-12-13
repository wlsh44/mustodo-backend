package mustodo.backend.auth.ui;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mustodo.backend.auth.ui.dto.EmailAuthDto;
import mustodo.backend.auth.ui.dto.LoginDto;
import mustodo.backend.auth.ui.dto.SignUpRequestDto;
import mustodo.backend.auth.ui.dto.UserResponse;
import mustodo.backend.user.domain.User;
import mustodo.backend.auth.application.AuthService;
import mustodo.backend.user.domain.embedded.Image;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    public static final String LOGIN_SESSION_ID = "LOGIN_USER";
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody @Valid LoginDto dto, HttpServletRequest request) {
        User user = authService.login(dto);

        HttpSession session = request.getSession();
        session.setAttribute(LOGIN_SESSION_ID, user);
        return ResponseEntity.ok(UserResponse.from(user, Image.getBaseUrl(request)));
    }

    @PostMapping("/sign-up")
    public void signUp(@RequestBody @Valid SignUpRequestDto dto) {
        authService.signUp(dto);
    }

    @PatchMapping("/authorization")
    @ResponseStatus(HttpStatus.OK)
    public void authorizeUser(@RequestBody @Valid EmailAuthDto dto) {
        authService.authorizeUser(dto);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session != null) {
            session.invalidate();
        }
    }
}
