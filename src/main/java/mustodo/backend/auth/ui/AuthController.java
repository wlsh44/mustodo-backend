package mustodo.backend.auth.ui;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mustodo.backend.dto.MessageDto;
import mustodo.backend.auth.ui.dto.EmailAuthDto;
import mustodo.backend.auth.ui.dto.LoginDto;
import mustodo.backend.auth.ui.dto.SignUpRequestDto;
import mustodo.backend.auth.domain.User;
import mustodo.backend.auth.application.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import static mustodo.backend.enums.response.AuthResponseMsg.LOGIN_SUCCESS;
import static mustodo.backend.enums.response.AuthResponseMsg.LOGOUT_SUCCESS;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    public static final String LOGIN_SESSION_ID = "LOGIN_USER";
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<MessageDto> login(@RequestBody @Valid LoginDto dto, HttpServletRequest request) {
        User user = authService.login(dto);

        HttpSession session = request.getSession();
        session.setAttribute(LOGIN_SESSION_ID, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sign-up")
    public ResponseEntity<MessageDto> signUp(@RequestBody @Valid SignUpRequestDto dto) {
        authService.signUp(dto);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/authorization")
    public ResponseEntity<MessageDto> authorizeUser(@RequestBody @Valid EmailAuthDto dto) {
        authService.authorizeUser(dto);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageDto> logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok().build();
    }
}
