package mustodo.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mustodo.backend.dto.MessageDto;
import mustodo.backend.dto.user.EmailAuthDto;
import mustodo.backend.dto.user.SignUpRequestDto;
import mustodo.backend.service.user.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    @PostMapping("/user")
    public ResponseEntity<MessageDto> signUp(@RequestBody @Valid SignUpRequestDto dto) {
        MessageDto messageDto = authService.signUp(dto);


        return new ResponseEntity<>(messageDto, HttpStatus.OK);
    }

    @PutMapping("/user/mail")
    public ResponseEntity<MessageDto> authorizeUser(@RequestBody @Valid EmailAuthDto dto) {
        MessageDto messageDto = authService.authorizeUser(dto);

        return new ResponseEntity<>(messageDto, HttpStatus.OK);
    }
}
