package com.dubu.backend.todo.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ParentInfo{
    @Column(name = "parent_id")
    private Long parentId;

    @Column(name =  "parent_version")
    private Long parentVersion;
}
