import styled from 'styled-components';

export const DayDateContainer = styled.div`
  display: flex;
  align-items: center;
`;

export const DayDateText = styled.div`
  ${({ theme }) => theme.fonts.body15Med};
  color: ${({ theme }) => theme.colors.gray800};
`;
