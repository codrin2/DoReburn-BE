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

export const MoodContainer = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  margin-top: 2rem;
`;

export const MoodItem = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.8rem;
`;

export const MoodImage = styled.img`
  width: 8.4rem;
  height: 8.4rem;
  border-radius: 50%;
  border: 0.3rem solid ${({ theme }) => theme.colors.green100};
  object-fit: contain;
`;

export const MoodAnimationLable = styled.span`
  ${({ theme }) => theme.fonts.label13Med};
  color: ${({ theme }) => theme.colors.gray400};
`;

export const MoodCount = styled.span`
  ${({ theme }) => theme.fonts.body16};
  color: ${({ theme }) => theme.colors.green800};
`;
