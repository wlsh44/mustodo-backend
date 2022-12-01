package mustodo.backend.sns.domain;

import lombok.NoArgsConstructor;
import lombok.ToString;
import mustodo.backend.user.domain.User;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@ToString
@NoArgsConstructor
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "following_user_id", nullable = false)
    private User following;

    @ManyToOne
    @JoinColumn(name = "follower_user_id", nullable = false)
    private User follower;

    public Follow(User following, User follower) {
        this.following = following;
        this.follower = follower;
    }
}
