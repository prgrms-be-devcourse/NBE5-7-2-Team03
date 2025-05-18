package com.team573.gongguri.domain.member.service;

import com.team573.gongguri.domain.member.dto.JoinRequestDto;
import com.team573.gongguri.domain.member.entity.Member;
import com.team573.gongguri.domain.member.entity.Univ;
import com.team573.gongguri.domain.member.mapper.MemberMapper;
import com.team573.gongguri.domain.member.repository.MemberRepository;
import com.team573.gongguri.domain.member.repository.UnivRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final UnivRepository univRepository;
    private final PasswordEncoder passwordEncoder;

    public void join(JoinRequestDto joinRequestDto) {
        Univ univ = univRepository.findByUnivName(joinRequestDto.univName()).orElse(null);
        if (univ == null) {
            univ = univRepository.save(new Univ(joinRequestDto.univName()));
        }

        String encodedPassword = passwordEncoder.encode(joinRequestDto.password());

        Member member = MemberMapper.toEntity(joinRequestDto, encodedPassword, univ);

        memberRepository.save(member);
    }
}