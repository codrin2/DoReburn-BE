package com.dubu.backend.todo.domain;

import com.dubu.backend.todo.domain.enums.MemberStatus;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Entity(name = "TodoMember")
@Table(name = "member")
@Getter
public class Member {
    @Id
    @Column(name = "member_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @ManyToMany
    @JoinTable(
            name = "member_category",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();
}
