import styled from 'styled-components';

import IconButton from '@/components/Button/IconButton';

export const CategoryRankLayout = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
`;

export const CategoryRankHeader = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
`;

export const HeaderWrapper = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

export const IconButtonWrapper = styled(IconButton)`
  padding: 1.2rem;
  border-radius: 50%;
  box-shadow: 2px 4px 4px rgba(0, 0, 0, 0.2);

  &:active {
    filter: brightness(0.9);
    background-color: ${({ theme }) => theme.colors.gray50};
  }
`;

export const HeaderTitle = styled.p`
  display: flex;
  flex-direction: column;

  ${({ theme }) => theme.fonts.headline18};
  color: ${({ theme }) => theme.colors.gray950};

  white-space: pre-wrap;
`;

export const SloganWrapper = styled.p`
  ${({ theme }) => theme.fonts.label14Med};
  color: ${({ theme }) => theme.colors.gray900};

  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 1.2rem 0;
  background-color: ${({ theme }) => theme.colors.gray50};
  border-radius: 0.8rem;

  white-space: pre-wrap;
  text-align: center;
`;

export const CategoryRankList = styled.div`
  display: flex;
  flex-direction: column;
  gap: 3.2rem;
  max-height: 13.6rem;

  padding: 0 2.4rem;
`;

export const BlankCategoryRank = styled.div`
  ${({ theme }) => theme.fonts.label14Med};
  color: ${({ theme }) => theme.colors.gray900};
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 2.4rem 0;
`;
