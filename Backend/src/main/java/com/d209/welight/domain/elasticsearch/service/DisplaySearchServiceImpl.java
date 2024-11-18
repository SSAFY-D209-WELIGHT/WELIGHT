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
    public Page<DisplayDocument> search(String userNickname, String keyword, Pageable pageable) {
        try {
            // userId로 검색하는 경우
            if (userNickname != null && !userNickname.isEmpty()) {
                // userId로 UserDocument 검색 - 퍼지 매칭 적용
                Page<UserDocument> userResults = userSearchRepository.findByUserNicknameContaining(userNickname, pageable);
                if (userResults.isEmpty()) {
                    throw new NoSearchResultException("해당하는 결과가 없습니다.");
                }

                // 찾은 모든 사용자의 UID 목록 생성
                List<Long> creatorUids = userResults.getContent().stream()
                        .map(UserDocument::getUserUid)
                        .collect(Collectors.toList());

                log.info("creatorUids로 검색: {}", creatorUids);
                
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
                
                log.info("키워드로 검색: {}", displayResults);
                return displayResults;
            }
            
            log.info("게시된 모든 디스플레이 조회");
            return displaySearchRepository.findByDisplayIsPostedTrue(pageable);

        } catch (NoSearchResultException e) {
            log.error("해당하는 결과가 없습니다");
            throw new NoSearchResultException("해당하는 결과가 없습니다.");
        }
    }
}