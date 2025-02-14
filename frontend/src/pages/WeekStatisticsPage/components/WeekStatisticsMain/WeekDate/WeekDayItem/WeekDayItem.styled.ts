import styled from 'styled-components';

export const WeekDayItemContainer = styled.li`
  width: 3.8rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
`;

export const WeekDayItemDate = styled.div`
  ${({ theme }) => theme.fonts.body15Med};
  color: ${({ theme }) => theme.colors.gray700};
`;
