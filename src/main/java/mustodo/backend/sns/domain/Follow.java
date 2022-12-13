package mustodo.backend.sns.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import mustodo.backend.user.domain.User;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Entity
@ToString
@NoArgsConstructor
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "following_id", nullable = false)
    private User following;

    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    public Follow(User follower, User following) {
        this.follower = follower;
        this.following = following;
    }
}
