package com.dubu.backend.todo.infrastructure;

import com.dubu.backend.plan.core.exception.SubPathNotFoundException;
import com.dubu.backend.plan.domain.SubPath;
import com.dubu.backend.todo.domain.repository.SubPathRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SubPathRepositoryImpl implements SubPathRepository {
    private final com.dubu.backend.plan.domain.repository.SubPathRepository subPathRepository;

    @Override
    public int findSectionTimeOfSubPath(Long subPathId) {
        SubPath subPath = subPathRepository.findById(subPathId)
                .orElseThrow(() -> new SubPathNotFoundException(subPathId));

        return subPath.getSectionTime();
    }
}
