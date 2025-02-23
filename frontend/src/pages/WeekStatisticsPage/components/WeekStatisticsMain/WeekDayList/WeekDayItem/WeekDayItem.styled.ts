import styled from 'styled-components';

export const WeekDayItemContainer = styled.li`
  width: 3.8rem;
`;

export const DayButton = styled.button`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  &:disabled {
    cursor: not-allowed;
  }
`;

export const Day = styled.div<{ $isToday: boolean; $isDisabledDay: boolean }>`
  width: 3.6rem;
  height: 3.6rem;
  display: flex;
  align-items: center;
  justify-content: center;
  ${({ theme }) => theme.fonts.body15Med};
  color: ${({ theme, $isToday, $isDisabledDay }) => {
    if ($isToday) {
      return theme.colors.green700;
    } else if ($isDisabledDay) {
      return theme.colors.gray300;
    } else {
      return theme.colors.gray800;
    }
  }};
  background-color: ${({ $isToday, theme }) => ($isToday ? theme.colors.green50 : 'transparent')};
  border-radius: 50%;
`;

export const DayUsageTime = styled.div`
  ${({ theme }) => theme.fonts.caption12Reg};
  color: ${({ theme }) => theme.colors.gray400};
`;
