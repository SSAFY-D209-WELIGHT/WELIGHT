package com.d209.welight.domain.elasticsearch.service;

import com.d209.welight.domain.elasticsearch.document.DisplayDocument;
import com.d209.welight.domain.elasticsearch.document.UserDocument;
import com.d209.welight.domain.elasticsearch.repository.DisplaySearchRepository;
import com.d209.welight.domain.elasticsearch.repository.UserSearchRepository;
import com.d209.welight.global.exception.elasticsearch.NoSearchResultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DisplaySearchServiceImpl implements DisplaySearchService {
    private final DisplaySearchRepository displaySearchRepository;
    private final UserSearchRepository userSearchRepository;

    @Override
    public Page<DisplayDocument> search(String userId, String keyword, Pageable pageable) {
        try {
            // userId로 검색하는 경우
            if (userId != null && !userId.isEmpty()) {
                // userId로 UserDocument 검색 - 퍼지 매칭 적용
                Page<UserDocument> userResults = userSearchRepository.findByUserIdContaining(userId, pageable);
                if (userResults.isEmpty()) {
                    throw new NoSearchResultException("해당하는 결과가 없습니다.");
                }

                // 찾은 모든 사용자의 UID 목록 생성
                List<Long> creatorUids = userResults.getContent().stream()
                        .map(UserDocument::getUserUid)
                        .collect(Collectors.toList());


                // keyword가 없는 경우 creatorUids로만 검색
                return displaySearchRepository.findByCreatorUidInAndDisplayIsPostedTrue(creatorUids, pageable);
            }

            // 키워드로만 검색
            if (keyword != null && !keyword.isEmpty()) {
                Page<DisplayDocument> displayResults = displaySearchRepository.findByDisplayNameContainingOrTagsContainingAndDisplayIsPostedTrue(
                        keyword,  // displayName 검색용
                        keyword,  // tags 검색용
                        pageable
                );
                if (displayResults.isEmpty()) {
                    throw new NoSearchResultException("해당하는 결과가 없습니다.");
                }

                return displayResults;
            }

            return displaySearchRepository.findByDisplayIsPostedTrue(pageable);

        } catch (NoSearchResultException e) {
            log.error("해당하는 결과가 없습니다");
            throw new NoSearchResultException("해당하는 결과가 없습니다.");
        }
    }
}