package mustodo.backend.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "images.profile")
public class ImageConfig {
    private final String rootPath;
    private final String path;
    private final String defaultImage;

    public ImageConfig(String rootPath, String path, String defaultImage) {
        this.rootPath = System.getProperty("user.dir") + rootPath;
        this.path = path;
        this.defaultImage = defaultImage;
    }
}
