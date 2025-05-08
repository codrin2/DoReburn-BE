package com.dubu.backend.todo.domain;

import com.dubu.backend.core.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Schedule extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    @Column(name = "date", columnDefinition = "DATE", nullable = false)
    private LocalDate date;

    @Column(name = "member_id")
    private Long memberId;

    public static Schedule of(Long memberId, LocalDate date){
        return Schedule.builder()
                .memberId(memberId)
                .date(date)
                .build();
    }

    public boolean matchesDate(LocalDate date){
        return this.date.equals(date);
    }
}
