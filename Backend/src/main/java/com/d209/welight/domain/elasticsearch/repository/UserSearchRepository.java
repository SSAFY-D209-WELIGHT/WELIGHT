package com.d209.welight.domain.elasticsearch.repository;

import com.d209.welight.domain.elasticsearch.Document.UserDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
}
