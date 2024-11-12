package com.d209.welight.domain.elasticsearch.repository;

import com.d209.welight.domain.elasticsearch.document.DisplayDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DisplaySearchRepository extends ElasticsearchRepository<DisplayDocument, String> {

    void deleteByDisplayUid(Long displayUid);

    @Query("{" +
            "\"bool\": {" +
            "\"should\": [" +
            "{\"wildcard\": {\"displayName\": {\"value\": \"*?0*\"}}}," +
            "{\"match\": {\"displayName\": {\"query\": \"?0\", \"fuzziness\": \"AUTO\"}}}," +
            "{\"wildcard\": {\"tags\": {\"value\": \"*?0*\"}}}," +
            "{\"match\": {\"tags\": {\"query\": \"?0\", \"fuzziness\": \"AUTO\"}}}" +
            "]" +
            "}" +
            "}")
    Page<DisplayDocument> findByDisplayNameContainingOrTagsContaining(String displayName, String tag, Pageable pageable);

    Page<DisplayDocument> findByCreatorUidIn(List<Long> creatorUids, Pageable pageable);

}