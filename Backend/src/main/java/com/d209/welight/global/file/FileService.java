package com.d209.welight.global.file;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    /**
     * presigned url 발급
     * @param prefix 버킷 디렉토리 이름
     * @param fileName 클라이언트가 전달한 파일명 파라미터
     * @param type true : 업로드 url / false : 다운로드 url 발급
     * @return presigned url
     */
    public Map<String, String> getPresignedUrl(String prefix, String fileName, Boolean type)  {
        if (type) {
            fileName = createPath(prefix, fileName); // "{prefix}/{uuid}-{fileName}"형식
        }
        if(fileName == null) {
            fileName = "aa";
        }

        GeneratePresignedUrlRequest generatePresignedUrlRequest = type ? getGeneratePresignedUrlForUpload(bucket, fileName) :
                getGeneratePresignedUrlForDownload(bucket, fileName);

        // Presigned URL 생성
        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
        return Map.of("url",url.toString(), "filePath", fileName);
    }

    /**
     * 파일 다운로드용(GET) presigned URL 생성 메서드
     * @param bucket S3 버킷 이름
     * @param fileName S3에 저장된 파일 이름
     * @return 다운로드 presigned URL을 위한 GeneratePresignedUrlRequest 객체
     */
    private GeneratePresignedUrlRequest getGeneratePresignedUrlForDownload(String bucket, String fileName) {
        // GET 요청을 위한 presigned URL 생성 요청
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, fileName)
                .withMethod(HttpMethod.GET)
                .withExpiration(getPresignedUrlExpiration());  // URL의 유효 기간 설정

        // 파일에 대해 Public Read 권한 설정
        generatePresignedUrlRequest.addRequestParameter(
                Headers.S3_CANNED_ACL,
                CannedAccessControlList.PublicRead.toString()
        );

        return generatePresignedUrlRequest;
    }

    /**
     * 파일 업로드용(PUT) presigned url 생성
     * @param bucket 버킷 이름
     * @param fileName S3 업로드용 파일 이름
     * @return 업로드 presigned URL을 위한 GeneratePresignedUrlRequest 객체
     */
    private GeneratePresignedUrlRequest getGeneratePresignedUrlForUpload(String bucket, String fileName) {
        // PUT 요청을 위한 presigned URL 생성 요청
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, fileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(getPresignedUrlExpiration()); // URL의 유효 기간 설정

        // 파일에 대해 Public Read 권한 설정
        generatePresignedUrlRequest.addRequestParameter(
                Headers.S3_CANNED_ACL,
                CannedAccessControlList.PublicRead.toString()
        );

        return generatePresignedUrlRequest;
    }

    /**
     * presigned url 유효 기간 설정
     * @return Presigned URL의 만료 시간
     */
    private Date getPresignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 10; // 10분 후
        expiration.setTime(expTimeMillis);

        return expiration;
    }

    /**
     * 파일 고유 ID(UUID) 생성
     * @return 36자리의 UUID (파일명에 사용)
     */
    private String createFileId() {
        return UUID.randomUUID().toString();
    }

    /**
     * 파일의 전체 경로를 생성
     * 고유한 파일 ID 생성 후 디렉토리 경로와 결합하여 전체 파일 경로를 생성
     * @param prefix 디렉토리 경로
     * @param fileName 클라이언트가 전달한 파일명
     * @return 파일의 전체 경로 (디렉토리 경로 + 파일 ID + 파일명)
     */
    private String createPath(String prefix, String fileName) {
        String fileId = createFileId();
        return String.format("%s/%s", prefix, fileId + "-" + fileName); // "{prefix}/{uuid}-{fileName}"형식
    }
}