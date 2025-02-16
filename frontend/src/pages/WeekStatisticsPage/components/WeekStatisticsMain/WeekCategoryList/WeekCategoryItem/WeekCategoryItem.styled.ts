import styled from 'styled-components';

export const WeekCategoryItemContainer = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 2rem 1.3rem;
  border-bottom: 0.15rem solid ${({ theme }) => theme.colors.gray100};
`;

export const CategoryLeftContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 2.3rem;
`;

export const CategoryTimeContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 0.8rem;
`;

export const CategoryIndex = styled.div`
  color: ${({ theme }) => theme.colors.gray400};
  ${({ theme }) => theme.fonts.heading22};
`;

export const CategoryTime = styled.div`
  color: ${({ theme }) => theme.colors.gray700};
  ${({ theme }) => theme.fonts.title24};
`;

export const CategoryCount = styled.div`
  color: ${({ theme }) => theme.colors.gray500};
  ${({ theme }) => theme.fonts.body15Med};
`;
