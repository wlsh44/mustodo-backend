package mustodo.backend.user.application;

import mustodo.backend.common.support.DatabaseClearer;
import mustodo.backend.config.ImageConfig;
import mustodo.backend.user.domain.Role;
import mustodo.backend.user.domain.User;
import mustodo.backend.user.domain.UserRepository;
import mustodo.backend.user.domain.embedded.EmailAuth;
import mustodo.backend.user.domain.embedded.Image;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class UserServiceTest {

    @Autowired
    ImageConfig imageConfig;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DatabaseClearer clearer;

    User user;

    @BeforeEach
    void init() {
        user = new User(1L, "test2@test.test", "test2", "test", Role.USER, "", null, new EmailAuth("123456", true));
        userRepository.save(user);
    }

    @AfterEach
    void clear() {
        File testDir = new File(imageConfig.getProfile());
        Arrays.stream(testDir.listFiles())
                .forEach(File::delete);
        testDir.deleteOnExit();
        clearer.clear();
    }

    @Test
    @DisplayName("이미지 파일에 해당하는 프로필 사진을 저장한다")
    void updateProfileImageTest() {
        //given
        String originalFilename = "testFileName.jpeg";
        MockMultipartFile testImageFile = new MockMultipartFile("fileName", originalFilename, "image/jpeg", "testImageString".getBytes());

        //when
        userService.updateProfileImage(user, testImageFile);

        //then
        Image profile = user.getProfile();
        assertThat(profile).isNotNull();
        assertThat(profile.getFileOriName()).isEqualTo(originalFilename);
        assertThat(profile.getFileUrl()).isEqualTo(imageConfig.getProfile());
    }
}