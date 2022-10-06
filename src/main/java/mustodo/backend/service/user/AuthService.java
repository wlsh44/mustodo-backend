package mustodo.backend.service.user;

import lombok.RequiredArgsConstructor;
import mustodo.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;


}
