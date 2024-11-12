package com.d209.welight.domain.elasticsearch.service;

import com.d209.welight.domain.display.entity.Display;
import com.d209.welight.domain.elasticsearch.event.DisplayEvent;
import com.d209.welight.domain.elasticsearch.event.UserEvent;
import com.d209.welight.domain.display.repository.DisplayRepository;
import com.d209.welight.domain.elasticsearch.listener.DisplayEventListener;
import com.d209.welight.domain.elasticsearch.listener.UserEventListener;
import com.d209.welight.domain.user.entity.User;
import com.d209.welight.domain.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchSyncService {
    private final DisplayRepository displayRepository;
    private final UserRepository userRepository;
    private final DisplayEventListener displayEventListener;
    private final UserEventListener userEventListener;

    @PostConstruct
    public void initialSync() {
        syncUsers();
        syncDisplays();
    }

    private void syncUsers() {
        log.info("사용자 Elasticsearch 초기 동기화를 시작합니다...");
        try {
            long count = userRepository.count();
            int batchSize = 100;
            int pages = (int) Math.ceil(count / (double) batchSize);

            for (int i = 0; i < pages; i++) {
                PageRequest pageRequest = PageRequest.of(i, batchSize);
                Page<User> users = userRepository.findAll(pageRequest);

                users.getContent().forEach(user ->
                        userEventListener.handleUserEvent(new UserEvent("CREATE", user))
                );

                log.info("사용자 배치 동기화 진행중: {}/{}", i + 1, pages);
            }
            log.info("사용자 Elasticsearch 초기 동기화가 완료되었습니다");
        } catch (Exception e) {
            log.error("사용자 초기 동기화 중 오류 발생: ", e);
            throw new RuntimeException("사용자 초기 동기화 실패", e);
        }
    }

    private void syncDisplays() {
        log.info("디스플레이 Elasticsearch 초기 동기화를 시작합니다...");
        try {
            long count = displayRepository.count();
            int batchSize = 100;
            int pages = (int) Math.ceil(count / (double) batchSize);

            for (int i = 0; i < pages; i++) {
                PageRequest pageRequest = PageRequest.of(i, batchSize);
                Page<Display> displays = displayRepository.findAll(pageRequest);

                displays.getContent().forEach(display ->
                        displayEventListener.handleDisplayEvent(new DisplayEvent("CREATE", display))
                );

                log.info("디스플레이 배치 동기화 진행중: {}/{}", i + 1, pages);
            }
            log.info("디스플레이 Elasticsearch 초기 동기화가 완료되었습니다");
        } catch (Exception e) {
            log.error("디스플레이 초기 동기화 중 오류 발생: ", e);
            throw new RuntimeException("디스플레이 초기 동기화 실패", e);
        }
    }
}