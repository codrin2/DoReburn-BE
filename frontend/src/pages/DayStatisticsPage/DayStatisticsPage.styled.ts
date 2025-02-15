import styled from 'styled-components';

export const DayStatisticsPageContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2.4rem;
  width: 100%;
  height: 100%;
  overflow: hidden;
`;

export const DayStatisticsContent = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  gap: 4rem;
  overflow-y: scroll;
`;

export const DayEmpty = styled.div`
  color: ${({ theme }) => theme.colors.gray400};
  ${({ theme }) => theme.fonts.heading20};
  height: 100%;
  margin-top: 3rem;
`;
