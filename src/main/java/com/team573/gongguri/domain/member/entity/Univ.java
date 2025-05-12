package com.team573.gongguri.domain.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "univ")
public class Univ {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long univId;

    @Column(nullable = false)
    private String univName;

    public Univ(String univName) {
        this.univName = univName;
    }
}
