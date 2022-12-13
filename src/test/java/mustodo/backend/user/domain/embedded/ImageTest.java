package mustodo.backend.user.domain.embedded;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class ImageTest {

    public static final String TEST_FILE_URL = "images/test/profile/";

    @AfterAll
    static void clear() {
        File testDir = new File(TEST_FILE_URL);
        Arrays.stream(testDir.listFiles())
                .forEach(File::delete);
        testDir.deleteOnExit();
    }

    @Test
    void 이미지_파일이_주어지면_올바른_이미지_객체가_만들어진다() {
        //given
        String originalFilename = "testFileName.jpeg";
        MockMultipartFile testImageFile = new MockMultipartFile("fileName", originalFilename, "image/jpeg", "testImageString".getBytes());

        //when
        Image image = Image.saveImage(testImageFile, TEST_FILE_URL);

        //then
        assertThat(image).isNotNull();
        assertThat(image.getFileOriName()).isEqualTo(originalFilename);
        assertThat(image.getFileUrl()).isEqualTo(TEST_FILE_URL);
    }
}