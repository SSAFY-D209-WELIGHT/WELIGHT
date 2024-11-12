package com.d209.welight.domain.elasticsearch.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Document(indexName = "display")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisplayDocument {
    @Id
    @Field(name = "display_uid", type = FieldType.Long)
    private Long displayUid;

    // 작성자 정보
    @Field(name = "creator_uid", type = FieldType.Long)
    private Long creatorUid;

    // 디스플레이 기본 정보
    @Field(name = "display_name", type = FieldType.Text)
    private String displayName;

    @Field(name = "display_thumbnail_url", type = FieldType.Keyword)
    private String displayThumbnailUrl;

    @Field(name = "display_is_posted", type = FieldType.Boolean)
    private Boolean displayIsPosted;

    @Field(name = "display_created_at", type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime displayCreatedAt;

    // 통계 정보
    @Field(name = "display_download_count", type = FieldType.Long)
    private Long displayDownloadCount;

    @Field(name = "display_like_count", type = FieldType.Long)
    private Long displayLikeCount;

    // 검색용 텍스트 정보
    @Field(name = "tags", type = FieldType.Text)
    private List<String> tags;

    @Field(name = "display_texts", type = FieldType.Text)
    private List<String> displayTexts;



}