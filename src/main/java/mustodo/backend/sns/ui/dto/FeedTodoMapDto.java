package mustodo.backend.sns.ui.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import mustodo.backend.sns.application.dto.FeedTodoQueryDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class FeedTodoMapDto {
    Map<FeedTodoKey, List<FeedTodoValue>> feedMap;

    public static FeedTodoMapDto from(List<FeedTodoQueryDto> todoDtoList, String baseUrl) {
        Map<FeedTodoKey, List<FeedTodoValue>> feedMap = new HashMap<>();

        todoDtoList.forEach(dto -> {
            String profilePath = baseUrl + dto.getProfile().getFileUrl() + dto.getProfile().getFileName();
            FeedTodoKey key = new FeedTodoKey(dto.getUserId(), dto.getUserName(), profilePath);
            FeedTodoValue value = new FeedTodoValue(dto.getTodoContent(), dto.getCategoryColor());
            feedMap.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        });
        return new FeedTodoMapDto(feedMap);
    }

    public List<FeedTodoDto> toFeedTodoDtoList() {
        return feedMap.entrySet().stream()
                .map(entry -> new FeedTodoDto(
                        entry.getKey().getUserId(),
                        entry.getKey().getUserName(),
                        entry.getKey().getProfilePath(),
                        entry.getValue())
                )
                .collect(Collectors.toList());
    }

    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static class FeedTodoKey {
        private Long userId;
        private String userName;
        private String profilePath;
    }
}
