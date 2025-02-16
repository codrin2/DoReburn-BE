import styled from 'styled-components';

export const DayTimeOverviewContainer = styled.div<{ $isMoved: boolean }>`
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-width: 32.7rem;
  min-height: 10.5rem;
  border: 0.15rem solid ${({ theme }) => theme.colors.gray200};
  padding: 0 1rem;
  border-radius: 2.8rem;
  opacity: ${({ $isMoved }) => ($isMoved ? 1 : 0.2)};
`;

export const TimeBox = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  gap: 0.8rem;
`;

export const TimeTitle = styled.div`
  ${({ theme }) => theme.fonts.headline17Reg};
  color: ${({ theme }) => theme.colors.gray700};
`;

export const TimeValue = styled.div`
  ${({ theme }) => theme.fonts.headline18};
  color: ${({ theme }) => theme.colors.gray900};
`;

export const Divider = styled.div`
  width: 0.15rem;
  height: 6.5rem;
  background-color: ${({ theme }) => theme.colors.gray200};
  border-radius: 0.6rem;
`;
