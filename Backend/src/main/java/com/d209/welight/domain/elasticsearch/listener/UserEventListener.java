package com.d209.welight.domain.elasticsearch.listener;

import com.d209.welight.domain.elasticsearch.document.UserDocument;
import com.d209.welight.domain.elasticsearch.repository.UserSearchRepository;
import com.d209.welight.domain.elasticsearch.event.UserEvent;
import com.d209.welight.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {
    private final UserSearchRepository userSearchRepository;

    @EventListener
    public void handleUserEvent(UserEvent event) {
        try {
            switch (event.getEventType()) {
                case "CREATE", "UPDATE" -> saveUser(event.getUser());
                case "DELETE" -> deleteUser(event.getUser());
                default -> log.warn("Unknown event type: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("Error processing user event: ", e);
            throw new RuntimeException("Failed to process user event", e);
        }
    }

    private void saveUser(User user) {
        UserDocument document = UserDocument.builder()
                .userUid(user.getUserUid())
                .userId(user.getUserId())
                .userNickname(user.getUserNickname())
                .userProfileImg(user.getUserProfileImg())
                .build();

        userSearchRepository.save(document);
        log.info("User document saved/updated: {}", user.getUserUid());
    }

    private void deleteUser(User user) {
        userSearchRepository.deleteByUserUid(user.getUserUid());
        log.info("User document deleted: {}", user.getUserUid());
    }
}
