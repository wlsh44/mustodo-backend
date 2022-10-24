package mustodo.backend.auth.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mustodo.backend.auth.application.mail.EmailAuthSender;
import mustodo.backend.auth.ui.dto.EmailAuthDto;
import mustodo.backend.auth.ui.dto.LoginDto;
import mustodo.backend.auth.ui.dto.SignUpRequestDto;
import mustodo.backend.auth.domain.User;
import mustodo.backend.auth.domain.embedded.EmailAuth;
import mustodo.backend.auth.domain.Role;
import mustodo.backend.exception.auth.IdPasswordNotCorrectException;
import mustodo.backend.exception.auth.InvalidEmailAuthKeyException;
import mustodo.backend.exception.auth.NotAuthorizedException;
import mustodo.backend.exception.auth.PasswordConfirmException;
import mustodo.backend.exception.auth.UncheckTermsAndConditionException;
import mustodo.backend.exception.user.EmailDuplicateException;
import mustodo.backend.exception.user.UserNameDuplicateException;
import mustodo.backend.exception.user.UserNotFoundException;
import mustodo.backend.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailAuthSender emailAuthSender;

    @Transactional
    public User login(LoginDto dto) {
        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(IdPasswordNotCorrectException::new);
        validatePassword(dto, user.getPassword());
        validateAuthorizedUser(user);

        return user;
    }

    private void validateAuthorizedUser(User user) {
        if (!user.isAuthorizedUser()) {
            String emailAuthKey = emailAuthSender.sendAuthMail(user);
            user.setEmailAuthKey(emailAuthKey);
            throw new NotAuthorizedException();
        }
    }

    private void validatePassword(LoginDto dto, String password) {
        if (!passwordEncoder.matches(dto.getPassword(), password)) {
            throw new IdPasswordNotCorrectException();
        }
    }

    @Transactional
    public void authorizeUser(EmailAuthDto dto) {
        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new UserNotFoundException(dto.getEmail()));
        validateEmailKey(dto, user.getEmailAuthKey());

        user.authorizeUser();
    }

    private void validateEmailKey(EmailAuthDto dto, String emailAuthKey) {
        if (!dto.getAuthKey().equals(emailAuthKey)) {
            throw new InvalidEmailAuthKeyException(dto.getAuthKey());
        }
    }

    @Transactional
    public Long signUp(SignUpRequestDto dto) {
        validateSignUpDto(dto);

        String encodedPassword = encodePassword(dto.getPassword());
        User user = toUserEntity(dto, encodedPassword);

        String emailAuthKey = emailAuthSender.sendAuthMail(user);

        User saveUser = userRepository.save(user);
        saveUser.setEmailAuthKey(emailAuthKey);

        return saveUser.getId();
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
            throw new UserNameDuplicateException(dto.getName());
        }
    }

    private void validatePassword(SignUpRequestDto dto) {
        String password = dto.getPassword();

        if (!password.equals(dto.getPasswordConfirm())) {
            throw new PasswordConfirmException();
        }
    }

    private void validateEmail(SignUpRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailDuplicateException(dto.getEmail());
        }
    }

    private void validateTermsAndCondition(boolean termsAndConditions) {
        if (!termsAndConditions) {
            throw new UncheckTermsAndConditionException();
        }
    }
}
