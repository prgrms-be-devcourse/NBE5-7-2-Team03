package com.team573.gongguri.domain.grouppurchase.controller;

import com.team573.gongguri.domain.grouppurchase.service.ImageUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
@Slf4j
public class ImageUploadController {

    private final ImageUploadService imageUploadService;
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestPart("imageFile") MultipartFile file) {
            Map<String, String> response = imageUploadService.uploadImage(file);
            return ResponseEntity.ok(response);
    }
}
