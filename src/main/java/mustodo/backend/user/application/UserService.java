package mustodo.backend.user.application;

import lombok.RequiredArgsConstructor;
import mustodo.backend.auth.ui.dto.UserResponse;
import mustodo.backend.config.ImageConfig;
import mustodo.backend.exception.user.UserNotFoundException;
import mustodo.backend.todo.domain.Todo;
import mustodo.backend.todo.domain.TodoRepository;
import mustodo.backend.todo.ui.dto.TodoMonthResponse;
import mustodo.backend.user.domain.User;
import mustodo.backend.user.domain.UserRepository;
import mustodo.backend.user.domain.embedded.Image;
import mustodo.backend.user.ui.dto.MeResponse;
import mustodo.backend.user.ui.dto.StatsMonthDto;
import mustodo.backend.user.ui.dto.StatsResponse;
import mustodo.backend.user.ui.dto.UpdateUserDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {

    private final ImageConfig imageConfig;
    private final EntityManager entityManager;
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;

    @Transactional
    public void updateProfileImage(User user, MultipartFile multipartFile) {
        User user1 = userRepository.findById(user.getId()).get();
        user1.updateProfileImage(multipartFile, imageConfig);
    }

    @Transactional(readOnly = true)
    public MeResponse getMe(User user) {
        return userRepository.findFollowCountByUser(user)
                .orElseThrow(() -> new UserNotFoundException(user.getEmail()));
    }

    @Transactional(readOnly = true)
    public StatsResponse findTodoStats(User user, String date) {
        YearMonth month = YearMonth.parse(date);
        List<StatsMonthDto> todoByMonth = todoRepository.findTodoStatsByMonth(user, month.atDay(1), month.atEndOfMonth());
        return new StatsResponse(todoByMonth);
    }

    @Transactional
    public UserResponse update(User user, UpdateUserDto dto, MultipartFile multipartFile, HttpServletRequest request) {
        User user1 = userRepository.findById(user.getId()).get();
        user1.update(dto, multipartFile, imageConfig);
        return UserResponse.from(user1, Image.getBaseUrl(request));
    }
}
