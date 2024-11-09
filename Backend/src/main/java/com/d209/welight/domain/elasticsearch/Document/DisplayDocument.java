package com.d209.welight.domain.elasticsearch.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

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

    @Field(name = "creator_nickname", type = FieldType.Text, analyzer = "korean_analyzer")
    private String creatorNickname;

    // 디스플레이 기본 정보
    @Field(name = "display_name", type = FieldType.Text, analyzer = "korean_analyzer")
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

    @Field(name = "comment_count", type = FieldType.Long)
    private Long commentCount;

    // 검색용 텍스트 정보
    @Field(name = "tags", type = FieldType.Text, analyzer = "korean_analyzer")
    private List<String> tags;

    @Field(name = "display_texts", type = FieldType.Text, analyzer = "korean_analyzer")
    private List<String> displayTexts;



}