import styled, { keyframes } from 'styled-components';

// 슬라이드 애니메이션
const slideInRight = keyframes`
  from {
    transform: translateX(100%);
  }
  to {
    transform: translateX(0);
  }
`;

const slideInLeft = keyframes`
  from {
    transform: translateX(-100%);
  }
  to {
    transform: translateX(0);
  }
`;

export const WeekDateContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2.4rem;
`;

export const WeekDateInfoContainer = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  ${({ theme }) => theme.fonts.body15Med};
  color: ${({ theme }) => theme.colors.gray600};
  gap: 2.4rem;
`;

export const WeekDayList = styled.ul`
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  gap: 1rem;

  /* 애니메이션 추가 */
  &.week-day-list {
    display: flex;
    transition: transform 0.3s ease-in-out;
    transform: translateX(0); /* 기본 상태 */
  }

  /* "슬라이드 왼쪽" 애니메이션 */
  &.prev-week {
    animation: ${slideInLeft} 0.5s forwards;
  }

  /* "슬라이드 오른쪽" 애니메이션 */
  &.next-week {
    animation: ${slideInRight} 0.5s forwards;
  }
`;
