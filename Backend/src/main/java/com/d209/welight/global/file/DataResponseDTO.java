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

    private String image;
    private String url;


    @Builder
    public DataResponseDTO( String image, String url) {
        this.image = image;
        this.url = url;
    }

    public static DataResponseDTO of(Map<String,String> map) {
        return DataResponseDTO.builder()
                .image(map.get("image"))
                .url(map.get("url"))
                .build();

    }
}