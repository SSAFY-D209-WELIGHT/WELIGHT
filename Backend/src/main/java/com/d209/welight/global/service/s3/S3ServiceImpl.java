package com.d209.welight.global.service.s3;

import com.d209.welight.global.util.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


@Service
@RequiredArgsConstructor
@Component
@Slf4j
public class S3ServiceImpl implements S3Service{

    private final S3Uploader s3Uploader;

    @Override
    @Transactional
    public String uploadS3(MultipartFile file, String type) throws IOException {
        String storedFileName = "";

        if(file != null)
            storedFileName = s3Uploader.uploadFileToS3(file, type);

        return storedFileName;
    }

    @Override
    @Transactional
    public String uploadS3(File file, String type) throws IOException {
        String storedFileName = "";
        if(file != null) {
            storedFileName = s3Uploader.uploadFileToS3(file, type);
        }
        return storedFileName;

    }

    @Override
    @Transactional
    public void deleteS3(String fileUrl) throws Exception {
        if(!Objects.equals(fileUrl, ""))
            s3Uploader.deleteS3(fileUrl);
    }

    @Override
    @Transactional
    public String copyS3(String srcFileUrl, String destFileUrl) throws Exception {
        if (!Objects.equals(srcFileUrl, "") && !Objects.equals(destFileUrl, "")) {
            String sourceKey = s3Uploader.extractKeyFromUrl(srcFileUrl);
            String destFileName = s3Uploader.extractKeyFromUrl(destFileUrl);
            return s3Uploader.copyAndRename(sourceKey, destFileName);
        }
        throw new IllegalArgumentException("원본 URL 또는 새 URL이 존재하지 않습니다.");
    }

}
