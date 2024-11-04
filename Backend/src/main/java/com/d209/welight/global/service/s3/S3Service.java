package com.d209.welight.global.service.s3;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface S3Service {
    // 멀티파트 파일 S3 업로드
    String uploadS3(MultipartFile file, String type) throws IOException;
    // 파일 S3 업로드
    String uploadS3(File file, String type) throws IOException;
    // S3 파일 삭제
    void deleteS3(String fileUrl) throws Exception;

    // 복사 및 이름 변경 업로드
    String copyS3(String srcFileUrl, String destFileUrl) throws Exception;
}
