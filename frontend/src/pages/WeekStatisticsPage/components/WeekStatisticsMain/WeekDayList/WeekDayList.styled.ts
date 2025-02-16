import styled from 'styled-components';

export const WeekDateContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
  padding: 0 1.6rem;
  gap: 2.4rem;
`;

export const WeekDateInfoContainer = styled.div`
  width: 26rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  ${({ theme }) => theme.fonts.body15Med};
  color: ${({ theme }) => theme.colors.gray600};
`;

export const WeekDayList = styled.ul`
  height: 6.8rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
`;
