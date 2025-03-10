package com.dubu.backend.member.core.exception;

import com.dubu.backend.core.exception.NotFoundException;

import static com.dubu.backend.core.exception.ErrorCode.MEMBER_NOT_FOUND;

public class MemberNotFoundException extends NotFoundException {
  public MemberNotFoundException(Long memberId) {
    super(MEMBER_NOT_FOUND.getMessage().formatted(memberId));
  }

  @Override
  public String getErrorCode() {
    return MEMBER_NOT_FOUND.name();
  }
}