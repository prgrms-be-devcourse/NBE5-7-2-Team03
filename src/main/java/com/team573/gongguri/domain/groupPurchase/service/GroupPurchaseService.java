package com.team573.gongguri.domain.groupPurchase.service;

import com.team573.gongguri.domain.chat.entity.ChatRoom;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseRequestDto;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseResponseDto;
import com.team573.gongguri.domain.groupPurchase.entity.GroupPurchase;
import com.team573.gongguri.domain.groupPurchase.mapper.GroupPurchaseMapper;
import com.team573.gongguri.domain.groupPurchase.repository.GroupPurchaseRepository;
import com.team573.gongguri.domain.member.entity.Member;
import com.team573.gongguri.domain.member.entity.Univ;
import com.team573.gongguri.global.exception.ErrorCode;
import com.team573.gongguri.global.exception.ErrorException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupPurchaseService {
    private final GroupPurchaseRepository repository;

    public GroupPurchaseResponseDto add(GroupPurchaseRequestDto dto, Member writer, ChatRoom chatRoom, Univ univ) {
        GroupPurchase entity = GroupPurchaseMapper.toEntity(dto, writer, chatRoom, univ);
        repository.save(entity);
        return GroupPurchaseMapper.toDto(entity);
    }

    public GroupPurchaseResponseDto get(Long id) {
        GroupPurchase entity = repository.findById(id)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_GROUP_PURCHASE));
        return GroupPurchaseMapper.toDto(entity);
    }

    public List<GroupPurchaseResponseDto> getAll() {
        return repository.findAll().stream()
                .map(GroupPurchaseMapper::toDto)
                .collect(Collectors.toList());
    }

    public GroupPurchaseResponseDto update(Long id, GroupPurchaseRequestDto dto) {
        GroupPurchase entity = repository.findById(id)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_GROUP_PURCHASE));
        entity.update(dto.title(), dto.content(), dto.price(), dto.maxParticipants(), dto.bank(), dto.account());
        return GroupPurchaseMapper.toDto(entity);
    }

    public void delete(Long id) {
        GroupPurchase entity = repository.findById(id)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_GROUP_PURCHASE));
        repository.delete(entity);
    }
}
