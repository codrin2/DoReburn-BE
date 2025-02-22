import styled from 'styled-components';

export const DayDateContainer = styled.div`
  display: flex;
  align-items: center;
`;

export const IconButton = styled.button`
  display: flex;
  align-items: center;
  justify-content: center;
  width: 3rem;
  height: 2.4rem;
`;

export const DayDateText = styled.div`
  ${({ theme }) => theme.fonts.body15Med};
  color: ${({ theme }) => theme.colors.gray800};
`;
