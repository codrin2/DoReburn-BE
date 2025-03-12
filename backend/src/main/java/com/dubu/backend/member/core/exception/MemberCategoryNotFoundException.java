package com.dubu.backend.member.core.exception;

import com.dubu.backend.core.exception.NotFoundException;

import static com.dubu.backend.core.exception.ErrorCode.*;

public class MemberCategoryNotFoundException extends NotFoundException {
    public MemberCategoryNotFoundException(Long memberId) {
        super(MEMBER_CATEGORY_NOT_FOUND.getMessage().formatted(memberId));
    }

    @Override
    public String getErrorCode() {
        return MEMBER_CATEGORY_NOT_FOUND.name();
    }
}
