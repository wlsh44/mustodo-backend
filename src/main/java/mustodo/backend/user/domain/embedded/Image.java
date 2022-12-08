package mustodo.backend.user.domain.embedded;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mustodo.backend.exception.user.ProfileImageSaveFailedException;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Embeddable;
import java.io.File;
import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Image {

    private String fileOriName;
    private String fileName;
    private String fileUrl;

    public static Image saveImage(MultipartFile multipartFile, String fileUrl) {
        if (multipartFile.isEmpty()) {
            return null;
        }
        try {
            String fileOriName = multipartFile.getOriginalFilename();
            String fileName = getFileName(fileOriName);
            File profileImage = new File(fileUrl + fileName);
            profileImage.getParentFile().mkdirs();
            multipartFile.transferTo(profileImage);
            return new Image(fileOriName, fileName, fileUrl);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ProfileImageSaveFailedException();
        }
    }

    private static String getFileName(String fileOriName) {
        String extWithDot = extractExtWithDot(fileOriName);
        String uuid = UUID.randomUUID().toString();
        return uuid + extWithDot;
    }

    private static String extractExtWithDot(String originalFilename) {
        int idx = originalFilename.lastIndexOf(".");
        return originalFilename.substring(idx);
    }
}