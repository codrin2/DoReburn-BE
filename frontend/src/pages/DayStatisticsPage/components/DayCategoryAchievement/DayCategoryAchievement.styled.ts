import styled from 'styled-components';

export const DayCategoryAchievementContainer = styled.div`
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 1rem;
`;

export const DayCategoryAchievementTitle = styled.div`
  ${({ theme }) => theme.fonts.body15};
  color: ${({ theme }) => theme.colors.gray600};
  margin-left: 3rem;
`;

export const CategoryList = styled.ul`
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin: 0 3rem;
`;

export const CategoryItem = styled.li`
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 0.15rem solid ${({ theme }) => theme.colors.gray100};
  padding: 1.2rem 0;
`;

export const CategoryCount = styled.div`
  ${({ theme }) => theme.fonts.body15Med};
  color: ${({ theme }) => theme.colors.gray500};
`;
