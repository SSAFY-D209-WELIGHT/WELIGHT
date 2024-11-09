package com.d209.welight.domain.elasticsearch.document;

import org.springframework.data.annotation.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDocument {

    @Id
    @Field(name = "user_uid", type = FieldType.Long)
    private long userUid;

    @Field(name = "user_id", type = FieldType.Text)
    private String userId;

    @Field(name = "user_nickname", type = FieldType.Text)
    private String userNickname;

    @Field(name = "user_profile_img", type = FieldType.Text)
    private String userProfileImg;

}
