package com.team573.gongguri.domain.groupPurchase.service;

import com.team573.gongguri.global.exception.CustomErrorCode;
import com.team573.gongguri.global.exception.CustomException;
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

    public Map<String, String> uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(CustomErrorCode.INVALID_IMAGE_FILE);
        }

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        File uploadPath = new File(UPLOAD_DIR);
        if (!uploadPath.exists()) {
            boolean created = uploadPath.mkdirs();
            log.info("업로드 폴더 생성됨: {}", created);
        }

        File dest = new File(uploadPath, filename);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            log.error("파일 저장 실패", e);
            throw new CustomException(CustomErrorCode.IMAGE_UPLOAD_FAILED);
        }

        String imageUrl = "/uploads/" + filename;
        log.info("이미지 저장 완료: {}", dest.getAbsolutePath());

        Map<String, String> response = new HashMap<>();
        response.put("imageUrl", imageUrl);
        return response;
    }

}
