package mustodo.backend.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mustodo.backend.dto.MessageDto;
import mustodo.backend.dto.user.EmailAuthDto;
import mustodo.backend.dto.user.SignUpRequestDto;
import mustodo.backend.entity.User;
import mustodo.backend.entity.embedded.EmailAuth;
import mustodo.backend.enums.Role;
import mustodo.backend.exception.UserException;
import mustodo.backend.repository.UserRepository;
import mustodo.backend.service.user.mail.EmailAuthSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static mustodo.backend.enums.error.SignUpErrorCode.ALREADY_EXIST_EMAIL;
import static mustodo.backend.enums.error.SignUpErrorCode.ALREADY_EXIST_NAME;
import static mustodo.backend.enums.error.SignUpErrorCode.INVALID_EMAIL_AUTH_KEY;
import static mustodo.backend.enums.error.SignUpErrorCode.NOT_EXIST_EMAIL;
import static mustodo.backend.enums.error.SignUpErrorCode.PASSWORD_CONFIRM_FAILED;
import static mustodo.backend.enums.error.SignUpErrorCode.UNCHECK_TERMS_AND_CONDITION;
import static mustodo.backend.enums.response.UserResponseMsg.EMAIL_AUTH_FAILED;
import static mustodo.backend.enums.response.UserResponseMsg.EMAIL_AUTH_SUCCESS;
import static mustodo.backend.enums.response.UserResponseMsg.SIGN_UP_FAILED;
import static mustodo.backend.enums.response.UserResponseMsg.SIGN_UP_SUCCESS;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailAuthSender emailAuthSender;

    @Transactional
    public MessageDto authorizeUser(EmailAuthDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UserException(EMAIL_AUTH_FAILED, NOT_EXIST_EMAIL));
        validateEmailKey(dto, user.getEmailAuthKey());

        user.authorizeUser();
        return MessageDto.builder()
                .message(EMAIL_AUTH_SUCCESS)
                .build();
    }

    private void validateEmailKey(EmailAuthDto dto, String emailAuthKey) {
        if (!dto.getAuthKey().equals(emailAuthKey)) {
            throw new UserException(EMAIL_AUTH_FAILED, INVALID_EMAIL_AUTH_KEY);
        }
    }

    @Transactional
    public MessageDto signUp(SignUpRequestDto dto) {
        validateSignUpDto(dto);

        String encodedPassword = encodePassword(dto.getPassword());
        User user = toUserEntity(dto, encodedPassword);

        String emailAuthKey = emailAuthSender.sendAuthMail(user);

        User saveUser = userRepository.save(user);
        saveUser.setEmailAuthKey(emailAuthKey);

        return MessageDto.builder()
                .message(SIGN_UP_SUCCESS)
                .build();
    }

    private User toUserEntity(SignUpRequestDto dto, String encodedPassword) {
        EmailAuth emailAuth = new EmailAuth(null, false);
        return User.builder()
                .email(dto.getEmail())
                .name(dto.getName())
                .password(encodedPassword)
                .role(Role.USER)
                .emailAuth(emailAuth)
                .build();
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private void validateSignUpDto(SignUpRequestDto dto) {
        validateTermsAndCondition(dto.isTermsAndConditions());
        validateEmail(dto);
        validateName(dto);
        validatePassword(dto);
    }

    private void validateName(SignUpRequestDto dto) {
        if (userRepository.existsByName(dto.getName())) {
            throw new UserException(SIGN_UP_FAILED, ALREADY_EXIST_NAME);
        }
    }

    private void validatePassword(SignUpRequestDto dto) {
        String password = dto.getPassword();

        if (!password.equals(dto.getPasswordConfirm())) {
            throw new UserException(SIGN_UP_FAILED, PASSWORD_CONFIRM_FAILED);
        }
    }

    private void validateEmail(SignUpRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new UserException(SIGN_UP_FAILED, ALREADY_EXIST_EMAIL);
        }
    }

    private void validateTermsAndCondition(boolean termsAndConditions) {
        if (!termsAndConditions) {
            throw new UserException(SIGN_UP_FAILED, UNCHECK_TERMS_AND_CONDITION);
        }
    }
}
