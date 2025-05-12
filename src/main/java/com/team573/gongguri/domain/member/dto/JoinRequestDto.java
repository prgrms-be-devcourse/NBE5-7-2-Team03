package com.team573.gongguri.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JoinRequestDto {
    private String email;
    private String nickname;
    private String password;
    private String univName;
}
