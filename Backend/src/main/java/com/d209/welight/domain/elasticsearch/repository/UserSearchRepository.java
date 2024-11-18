package com.d209.welight.domain.elasticsearch.repository;

import com.d209.welight.domain.elasticsearch.document.UserDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSearchRepository extends ElasticsearchRepository<UserDocument, String> {

    void deleteByUserUid(long userUid);

    @Query("{" +
            "\"bool\": {" +
            "\"should\": [" +
            "{\"wildcard\": {\"userId\": {\"value\": \"*?0*\"}}}," +
            "{\"match\": {\"userId\": {\"query\": \"?0\", \"fuzziness\": \"AUTO\"}}}" +
            "]" +
            "}" +
            "}")
    Page<UserDocument> findByUserIdContaining(String userId, Pageable pageable);

    @Query("{" +
            "\"bool\": {" +
            "\"should\": [" +
            "{\"wildcard\": {\"userNickname\": {\"value\": \"*?0*\"}}}," +
            "{\"match\": {\"userNickname\": {\"query\": \"?0\", \"fuzziness\": \"AUTO\"}}}" +
            "]" +
            "}" +
            "}")
    Page<UserDocument> findByUserNicknameContaining(String userNickname, Pageable pageable);
}
