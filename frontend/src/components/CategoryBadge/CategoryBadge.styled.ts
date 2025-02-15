import styled from 'styled-components';

import { CategoryType } from '@/types/filter';

export const CategoryBadge = styled.span<{ $category?: CategoryType }>`
  ${({ theme }) => theme.fonts.caption12Reg};
  color: ${({ theme, $category }) => ($category ? theme.colors[$category] : theme.colors.OTHERS)};
  background-color: ${({ theme, $category }) =>
    $category ? theme.colors.background[$category] : theme.colors.gray100};

  padding: 0.4rem 0.8rem;
  border-radius: 0.8rem;
`;
