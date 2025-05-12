package com.team573.gongguri.domain.groupPurchase.service;

import com.team573.gongguri.domain.groupPurchase.repository.GroupPurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupPurchaseService {
    private final GroupPurchaseRepository repository;

}
