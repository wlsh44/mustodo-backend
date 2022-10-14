package mustodo.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mustodo.backend.dto.MessageDto;
import mustodo.backend.dto.auth.EmailAuthDto;
import mustodo.backend.dto.auth.LoginDto;
import mustodo.backend.dto.auth.SignUpRequestDto;
import mustodo.backend.entity.User;
import mustodo.backend.service.user.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
@RequestMapping("/user")
@RequiredArgsConstructor
public class AuthController {

    public static final String LOGIN_SESSION_ID = "LOGIN_USER";
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<MessageDto> login(@RequestBody @Valid LoginDto dto, HttpServletRequest request) {
        User user = authService.login(dto);

        HttpSession session = request.getSession();
        session.setAttribute(LOGIN_SESSION_ID, user);
        MessageDto message = MessageDto.builder()
                .message(LOGIN_SUCCESS)
                .build();
        return ResponseEntity.ok(message);
    }

    @PostMapping("")
    public ResponseEntity<MessageDto> signUp(@RequestBody @Valid SignUpRequestDto dto) {
        MessageDto messageDto = authService.signUp(dto);

        return ResponseEntity.ok(messageDto);
    }

    @PutMapping("/auth")
    public ResponseEntity<MessageDto> authorizeUser(@RequestBody @Valid EmailAuthDto dto) {
        MessageDto messageDto = authService.authorizeUser(dto);

        return ResponseEntity.ok(messageDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageDto> logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session != null) {
            session.invalidate();
        }
        MessageDto messageDto = MessageDto.builder()
                .message(LOGOUT_SUCCESS)
                .build();

        return ResponseEntity.ok(messageDto);
    }
}
