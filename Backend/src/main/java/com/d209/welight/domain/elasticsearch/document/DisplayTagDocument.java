package com.d209.welight.domain.elasticsearch.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Document(indexName = "displaytag")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class DisplayTagDocument {

    @Id
    @Field(name = "display_tag_uid", type = FieldType.Long)
    private Long tagUid;

    @Field(type = FieldType.Long)
    private Long displayUid;

    @Field(name = "display_tag_text", type = FieldType.Text)
    private String displayTagText;

    @Field(name = "display_tag_created_at", type = FieldType.Date)
    private LocalDateTime displayTagCreatedAt;

}