import styled from 'styled-components';

export const WeekOverviewContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: ${({ theme }) => theme.colors.green25};
  border-radius: 2.8rem;
  margin: 0 1.6rem;
  padding: 2rem;
  gap: 3.2rem;
`;

export const WeekOverviewContent = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  gap: 2rem;
`;

export const WeekOverviewTitle = styled.div`
  ${({ theme }) => theme.fonts.body15Med};
  color: ${({ theme }) => theme.colors.gray500};
`;

export const WeekAchievement = styled.div`
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  ${({ theme }) => theme.fonts.title24};
`;

export const CompareLastWeek = styled.div`
  width: 100%;
  display: flex;
  justify-content: space-between;
  padding: 1.2rem 1.4rem;
  border-radius: 0.8rem;
  background-color: ${({ theme }) => theme.colors.white};
`;

export const CompareTitle = styled.div`
  ${({ theme }) => theme.fonts.body15Med};
  color: ${({ theme }) => theme.colors.gray400};
`;

export const CompareValue = styled.div`
  ${({ theme }) => theme.fonts.body15Med};
  color: ${({ theme }) => theme.colors.green800};
`;

export const TimeInfoWrapper = styled.div`
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-around;
  gap: 1.2rem;
`;

export const TimeInfoBox = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.7rem;
`;

export const TimeInfoLabel = styled.div`
  ${({ theme }) => theme.fonts.body15Med};
  color: ${({ theme }) => theme.colors.gray600};
`;

export const TimeInfoValue = styled.div`
  ${({ theme }) => theme.fonts.headline18};
  color: ${({ theme }) => theme.colors.gray800};
`;

export const Divider = styled.div`
  width: 0.15rem;
  height: 5rem;
  background-color: ${({ theme }) => theme.colors.gray200};
  border-radius: 0.1rem;
`;
