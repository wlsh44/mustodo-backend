package mustodo.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mustodo.backend.entity.embedded.EmailAuth;
import mustodo.backend.entity.embedded.Image;
import mustodo.backend.enums.Role;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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

    @Column(nullable = false, length = 65)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    private String biography;

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
}



