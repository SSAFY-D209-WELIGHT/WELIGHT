package com.d209.welight.domain.elasticsearch.service;

import com.d209.welight.domain.elasticsearch.document.DisplayDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DisplaySearchService {
    public Page<DisplayDocument> search(String userId, String keyword, Pageable pageable);

}
