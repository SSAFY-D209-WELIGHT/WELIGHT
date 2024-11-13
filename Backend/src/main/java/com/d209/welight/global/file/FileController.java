package com.d209.welight.global.file;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@CrossOrigin("*")
@Tag(name = "File Controller", description = "파일 API") // 표시명 및 설명 설정
public class FileController {


    private final FileService fileService;

    @GetMapping("/upload/{url}")
    @Operation(summary = "파일 업로드 url 받기", description = "파일 업로드 url을 받습니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "파일 업로드 url을 성공적으로 조회했습니다."),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.")
    })
    public ResponseEntity<?> getUploadPresignedUrl(@PathVariable String url) {
        DataResponseDTO data = DataResponseDTO.of(fileService.getPresignedUrl("images",url,true));

        return new ResponseEntity<DataResponseDTO>(data, HttpStatus.CREATED);

    }

    @GetMapping("/download/{url}")
    @Operation(summary = "파일 다운로드 url 받기", description = "파일 다운로드 url을 받습니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "파일 다운로드 url을 성공적으로 조회했습니다."),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.")
    })
    public ResponseEntity<?> getDownloadPresignedUrl(@PathVariable String url) {
        DataResponseDTO data = DataResponseDTO.of(fileService.getPresignedUrl("images",url,false));

        return new ResponseEntity<DataResponseDTO>(data,HttpStatus.CREATED);

    }
}