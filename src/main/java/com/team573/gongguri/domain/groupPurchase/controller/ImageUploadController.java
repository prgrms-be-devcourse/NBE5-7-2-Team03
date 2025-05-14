package com.team573.gongguri.domain.groupPurchase.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
@Slf4j
public class ImageUploadController {

    // ✅ Windows에서도 안전한 절대 경로 (유저 홈 디렉토리 하위)
    private static final String UPLOAD_DIR = System.getProperty("user.home") + "/gongguri-uploads";

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestPart("imageFile") MultipartFile file) {
        log.info("[ImageUploadController] 이미지 업로드 요청 수신");

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "이미지 파일이 비어있습니다."));
        }

        try {
            // 저장할 파일 이름 생성
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

            // 디렉토리 생성
            File uploadPath = new File(UPLOAD_DIR);
            if (!uploadPath.exists()) {
                boolean created = uploadPath.mkdirs();
                log.info("📁 업로드 폴더 생성됨: {}", created);
            }

            // 파일 저장
            File dest = new File(uploadPath, filename);
            file.transferTo(dest);

            // 접근 경로는 uploads/filename 으로 가정 (리버스 프록시나 NGINX에서 매핑 가능)
            String imageUrl = "/uploads/" + filename;
            log.info("✅ 이미지 저장 완료: {}", dest.getAbsolutePath());

            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            log.error("🚨 이미지 저장 중 오류 발생", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "파일 저장 중 오류 발생"));
        }
    }
}
