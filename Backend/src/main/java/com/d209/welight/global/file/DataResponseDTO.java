package com.d209.welight.global.file;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public class DataResponseDTO {

    private String filePath;
    private String url;


    @Builder
    public DataResponseDTO( String filePath, String url) {
        this.filePath = filePath;
        this.url = url;
    }

    public static DataResponseDTO of(Map<String,String> map) {
        return DataResponseDTO.builder()
                .filePath(map.get("filePath"))
                .url(map.get("url"))
                .build();

    }
}