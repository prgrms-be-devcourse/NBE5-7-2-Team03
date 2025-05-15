package com.team573.gongguri.domain.groupPurchase.repository;

import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseWithParticipantCountDto;
import com.team573.gongguri.domain.groupPurchase.entity.ProgressStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class GroupPurchaseJpqlRepository {

    @PersistenceContext
    private EntityManager em;

    // 참가자 수 세는 로직에 status 조건 들어가야함
    public List<GroupPurchaseWithParticipantCountDto> findWithCursorAndParticipantCount(Long cursorId, ProgressStatus status, int size) {
        String jpql = """
            SELECT new com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseWithParticipantCountDto(
                gp.groupId,
                gp.title,
                gp.content,
                gp.price,
                gp.maxParticipants,
                gp.progressStatus,
                gp.createdAt,
                cr.id,
                COUNT(p),
                gp.imageUrl
            )
            FROM GroupPurchase gp
            JOIN gp.chatRoom cr
            LEFT JOIN GroupPurchaseParticipant p ON p.groupPurchase = gp
            WHERE (:cursorId IS NULL OR gp.groupId < :cursorId)
            AND (:status IS NULL OR gp.progressStatus = :status)
            GROUP BY gp.groupId, gp.title, gp.content, gp.price, gp.maxParticipants, gp.progressStatus, gp.createdAt, cr.id
            ORDER BY gp.groupId DESC
            """;

        return em.createQuery(jpql, GroupPurchaseWithParticipantCountDto.class)
            .setParameter("cursorId", cursorId)
            .setParameter("status", status)
            .setMaxResults(size)
            .getResultList();
    }
}
