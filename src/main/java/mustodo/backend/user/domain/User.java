package mustodo.backend.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mustodo.backend.config.ImageConfig;
import mustodo.backend.sns.domain.Follow;
import mustodo.backend.user.domain.embedded.EmailAuth;
import mustodo.backend.user.domain.embedded.Image;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 321)
    private String email;

    @Column(nullable = false, length = 40)
    private String name;

    @Column(nullable = false, length = 70)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    private String biography;

    @OneToMany(mappedBy = "follower")
    private final List<Follow> followers = new ArrayList<>();

    @OneToMany(mappedBy = "following")
    private final List<Follow> followings = new ArrayList<>();

    @Embedded
    private Image profile;

    @Embedded
    private EmailAuth emailAuth;

    public void setEmailAuthKey(String emailAuthKey) {
        emailAuth.setEmailKey(emailAuthKey);
    }

    public String getEmailAuthKey() {
        return emailAuth.getEmailKey();
    }

    public void authorizeUser() {
        emailAuth.setEmailAuth(true);
    }

    public boolean isAuthorizedUser() {
        return emailAuth.isEmailAuth();
    }

    public void updateProfileImage(MultipartFile multipartFile, ImageConfig imageConfig) {
        this.profile = Image.saveImage(multipartFile, imageConfig);
    }
}