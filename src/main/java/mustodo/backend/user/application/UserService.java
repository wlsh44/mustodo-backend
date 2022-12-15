package mustodo.backend.user.application;

import lombok.RequiredArgsConstructor;
import mustodo.backend.config.ImageConfig;
import mustodo.backend.exception.user.UserNotFoundException;
import mustodo.backend.user.domain.User;
import mustodo.backend.user.domain.UserRepository;
import mustodo.backend.user.ui.dto.MeResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;

@Service
@RequiredArgsConstructor
public class UserService {

    private final ImageConfig imageConfig;
    private final EntityManager entityManager;
    private final UserRepository userRepository;

    @Transactional
    public void updateProfileImage(User user, MultipartFile multipartFile) {
        entityManager.merge(user);
        user.updateProfileImage(multipartFile, imageConfig);
    }

    @Transactional(readOnly = true)
    public MeResponse getMe(User user) {
        return userRepository.findFollowCountByUser(user)
                .orElseThrow(() -> new UserNotFoundException(user.getEmail()));
    }
}
