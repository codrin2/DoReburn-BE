import styled from 'styled-components';

export const MyPageGoalContainer = styled.div`
  display: flex;
  gap: 0.8rem;
`;

export const Title = styled.div`
  width: 4.8rem;
  color: ${({ theme }) => theme.colors.gray950};
  ${({ theme }) => theme.fonts.body15Med};
`;

export const GuideMessage = styled.div<{ $isError: boolean }>`
  color: ${({ theme, $isError }) => ($isError ? theme.colors.textRed : theme.colors.green700)};
  ${({ theme }) => theme.fonts.caption12Semi};
  padding: 0.4rem 0;
`;

export const GoalList = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;
  width: 100%;
`;

export const GoalRow = styled.div`
  display: flex;
  gap: 1rem;
  width: 100%;
`;

export const GoalItem = styled.button<{ isSelected: boolean }>`
  display: flex;
  align-items: center;
  gap: 0.4rem;
  border-radius: 0.8rem;
  padding: 0.4rem 1rem;
  background-color: ${({ isSelected, theme }) =>
    isSelected ? theme.colors.white : theme.colors.gray50};
  border: ${({ isSelected, theme }) =>
    isSelected ? `0.1rem solid ${theme.colors.green600}` : `0.1rem solid ${theme.colors.gray100}`};
`;

export const GoalItemText = styled.span<{ isSelected: boolean }>`
  ${({ theme }) => theme.fonts.label14Med};
  color: ${({ isSelected, theme }) => (isSelected ? theme.colors.green700 : theme.colors.gray300)};
`;
