package mustodo.backend.service.user;

import lombok.RequiredArgsConstructor;
import mustodo.backend.dto.MessageDto;
import mustodo.backend.dto.user.SignUpRequestDto;
import mustodo.backend.entity.User;
import mustodo.backend.exception.UserException;
import mustodo.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static mustodo.backend.enums.error.SignUpErrorCode.ALREADY_EXIST_EMAIL;
import static mustodo.backend.enums.error.SignUpErrorCode.PASSWORD_CONFIRM_FAILED;
import static mustodo.backend.enums.error.SignUpErrorCode.UNCHECK_TERMS_AND_CONDITION;
import static mustodo.backend.enums.response.UserResponseMsg.SIGN_UP_FAILED;
import static mustodo.backend.enums.response.UserResponseMsg.SIGN_UP_SUCCESS;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public MessageDto signUp(SignUpRequestDto dto) {
        validateTermsAndCondition(dto.isTermsAndConditions());
        validateEmail(dto);
        validatePassword(dto);

        String encodedPassword = encodePassword(dto.getPassword());
        User user = getUser(dto, encodedPassword);

        userRepository.save(user);

        return MessageDto.builder()
                .message(SIGN_UP_SUCCESS)
                .build();
    }

    private User getUser(SignUpRequestDto dto, String encodedPassword) {
        return User.builder()
                .email(dto.getEmail())
                .name(dto.getName())
                .password(encodedPassword)
                .build();
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
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
