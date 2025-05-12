package com.team573.gongguri.domain.member.repository;

import com.team573.gongguri.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
