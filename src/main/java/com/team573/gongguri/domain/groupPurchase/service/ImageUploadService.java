package com.team573.gongguri.domain.groupPurchase.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class ImageUploadService {
    private static final String UPLOAD_DIR = System.getProperty("user.home") + "/gongguri-uploads";

    public Map<String, String> uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 비어있습니다.");
        }

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        File uploadPath = new File(UPLOAD_DIR);
        if (!uploadPath.exists()) {
            boolean created = uploadPath.mkdirs();
            log.info("업로드 폴더 생성됨: {}", created);
        }

        File dest = new File(uploadPath, filename);
        file.transferTo(dest);

        String imageUrl = "/uploads/" + filename;
        log.info("이미지 저장 완료: {}", dest.getAbsolutePath());

        Map<String, String> response = new HashMap<>();
        response.put("imageUrl", imageUrl);
        return response;
    }

}
