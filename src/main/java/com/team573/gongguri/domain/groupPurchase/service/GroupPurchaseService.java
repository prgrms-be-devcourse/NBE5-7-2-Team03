package com.team573.gongguri.domain.groupPurchase.service;

import com.team573.gongguri.domain.chat.entity.ChatRoom;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseRequestDto;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseResponseDto;
import com.team573.gongguri.domain.groupPurchase.entity.GroupPurchase;
import com.team573.gongguri.domain.groupPurchase.entity.ProgressStatus;
import com.team573.gongguri.domain.groupPurchase.mapper.GroupPurchaseMapper;
import com.team573.gongguri.domain.groupPurchase.repository.GroupPurchaseRepository;
import com.team573.gongguri.domain.member.entity.Member;
import com.team573.gongguri.domain.member.entity.Univ;
import com.team573.gongguri.global.exception.ErrorCode;
import com.team573.gongguri.global.exception.ErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupPurchaseService {
    private final GroupPurchaseRepository repository;

    @Transactional
    public GroupPurchaseResponseDto add(GroupPurchaseRequestDto dto, Member writer, ChatRoom chatRoom, Univ univ) {
        try {
            GroupPurchase entity = GroupPurchaseMapper.toEntity(dto, writer, chatRoom, univ);
            entity.setImageUrl(dto.imageUrl()); // 이미지 URL 직접 설정

            repository.save(entity);
            return GroupPurchaseMapper.toDto(entity);
        } catch (Exception e) {
            throw new ErrorException(ErrorCode.CREATE_FAILED_GROUP_PURCHASE);
        }
    }

    @Transactional(readOnly = true)
    public GroupPurchaseResponseDto get(Long id) {
        GroupPurchase entity = repository.findById(id)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_GROUP_PURCHASE));
        return GroupPurchaseMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<GroupPurchaseResponseDto> getAll() {
        return repository.findAll().stream()
                .map(GroupPurchaseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public GroupPurchaseResponseDto update(Long id, GroupPurchaseRequestDto dto) {
        try {
            GroupPurchase entity = repository.findById(id)
                    .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_GROUP_PURCHASE));

            entity.update(
                    dto.title(),
                    dto.content(),
                    dto.price(),
                    dto.maxParticipants(),
                    dto.bank(),
                    dto.account(),
                    ProgressStatus.valueOf(dto.progressStatus().toUpperCase())
            );
            entity.setImageUrl(dto.imageUrl()); // 업데이트 시에도 imageUrl 반영

            return GroupPurchaseMapper.toDto(entity);
        } catch (Exception e) {
            throw new ErrorException(ErrorCode.UPDATE_FAILED_GROUP_PURCHASE);
        }
    }

    @Transactional
    public void delete(Long id) {
        try {
            GroupPurchase entity = repository.findById(id)
                    .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_GROUP_PURCHASE));
            repository.delete(entity);
        } catch (Exception e) {
            throw new ErrorException(ErrorCode.DELETE_FAILED_GROUP_PURCHASE);
        }
    }
}
