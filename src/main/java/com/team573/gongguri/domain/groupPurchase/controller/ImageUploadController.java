package com.team573.gongguri.domain.groupPurchase.controller;

import com.team573.gongguri.domain.groupPurchase.service.ImageUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
@Slf4j
public class ImageUploadController {

    private final ImageUploadService imageUploadService;
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestPart("imageFile") MultipartFile file) {
        try {
            Map<String, String> response = imageUploadService.uploadImage(file);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("이미지 저장 중 오류 발생", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "파일 저장 중 오류 발생"));
        }
    }
}
