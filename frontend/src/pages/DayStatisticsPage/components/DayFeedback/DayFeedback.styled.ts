import styled from 'styled-components';

export const DayFeedbackContainer = styled.div`
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 1.6rem;
`;

export const DayFeedbackTitle = styled.div`
  ${({ theme }) => theme.fonts.body15};
  color: ${({ theme }) => theme.colors.gray600};
  margin-left: 3rem;
`;

export const DayFeedbackList = styled.div`
  display: flex;
  gap: 1.2rem;
  overflow-x: auto;
  padding: 0 2rem;
`;

export const DayFeedbackItem = styled.div`
  min-width: 28.4rem;
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
  ${({ theme }) => theme.fonts.body15};
  color: ${({ theme }) => theme.colors.gray600};
  background-color: ${({ theme }) => theme.colors.green25};
  border-radius: 2.4rem;
  padding: 1.6rem;
`;

export const DayFeedbackMood = styled.div`
  ${({ theme }) => theme.fonts.headline17};
  color: ${({ theme }) => theme.colors.gray800};
`;

export const DayFeedbackMemo = styled.div`
  ${({ theme }) => theme.fonts.label14Med};
  color: ${({ theme }) => theme.colors.gray400};
  background-color: ${({ theme }) => theme.colors.white};
  border-radius: 0.8rem;
  padding: 1.2rem;
`;

export const DayFeedbackEmpty = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  height: 30%;
  ${({ theme }) => theme.fonts.caption12Reg};
  color: ${({ theme }) => theme.colors.gray400};
  text-align: center;
`;
