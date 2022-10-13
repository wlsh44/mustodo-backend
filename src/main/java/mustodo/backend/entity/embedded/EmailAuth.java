package mustodo.backend.entity.embedded;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Setter
@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EmailAuth {

    @Column(length = 6)
    private String emailKey;

    @Column(nullable = false)
    private boolean isEmailAuth;
}