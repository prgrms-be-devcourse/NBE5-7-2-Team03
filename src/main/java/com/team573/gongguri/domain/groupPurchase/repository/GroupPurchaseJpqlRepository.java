package com.team573.gongguri.domain.groupPurchase.repository;

import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseWithParticipantCountDto;
import com.team573.gongguri.domain.groupPurchase.entity.ProgressStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class GroupPurchaseJpqlRepository {

    @PersistenceContext
    private EntityManager em;

    public List<GroupPurchaseWithParticipantCountDto> findAllWithCursorAndParticipantCount(
            Long cursorId,
            List<ProgressStatus> statuses,
            int size
    ) {
        String jpql = """
            SELECT new com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseWithParticipantCountDto(
                gp.groupId,
                gp.title,
                gp.content,
                gp.price,
                gp.maxParticipants,
                gp.progressStatus,
                gp.createdAt,
                null,
                COUNT(p),
                gp.imageUrl
            )
            FROM GroupPurchase gp
            LEFT JOIN GroupPurchaseParticipant p ON p.groupPurchase.id = gp.id AND p.participationStatus = 'JOINED'
            WHERE (:cursorId IS NULL OR gp.groupId < :cursorId)
            AND (:statusesIsEmpty = true OR gp.progressStatus IN :statuses)
            AND gp.isDeleted = false
            GROUP BY gp.groupId
            ORDER BY gp.groupId DESC
        """;

        return em.createQuery(jpql, GroupPurchaseWithParticipantCountDto.class)
                .setParameter("cursorId", cursorId)
                .setParameter("statusesIsEmpty", statuses == null || statuses.isEmpty())
                .setParameter("statuses", statuses == null ? Collections.emptyList() : statuses)
                .setMaxResults(size)
                .getResultList();
    }
}
