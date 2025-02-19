import styled from 'styled-components';

import theme from '@/styles/theme';
import { CategoryType } from '@/types/filter';

export const RadioGroupWrapper = styled.div<{ $width?: string; $isFilter?: boolean }>`
  display: flex;
  justify-content: ${({ $isFilter }) => ($isFilter ? 'space-between' : 'flex-start')};
  gap: 1.3rem 0.8rem;
  width: ${({ $width }) => $width ?? '20rem'};
  flex-wrap: wrap;
`;

export const HiddenInput = styled.input`
  border: 0;
  clip: rect(0 0 0 0);
  height: 1px;
  margin: -1px;
  overflow: hidden;
  padding: 0;
  position: absolute;
  width: 1px;
`;

export const RadioBadge = styled.span<{
  $isSelected: boolean;
  $disabled?: boolean;
  $category?: CategoryType | null;
  $isFilter?: boolean;
}>`
  ${({ theme }) => theme.fonts.label14Reg};

  padding: ${({ $isFilter }) => ($isFilter ? '0.5rem 0.8rem' : '0.5rem 1.2rem')};
  border-radius: 0.8rem;

  background-color: ${({ theme, $isSelected }) =>
    $isSelected ? theme.colors.white : theme.colors.gray50};

  color: ${({ $isSelected, $category }) => getColor($isSelected, $category)};

  outline: ${({ theme, $isSelected }) =>
    $isSelected ? `0.1rem solid ${theme.colors.green600}` : 'none'};

  cursor: ${({ $disabled }) => ($disabled ? 'not-allowed' : 'pointer')};
  opacity: ${({ $disabled }) => ($disabled ? 0.9 : 1)};

  transition:
    background-color 0.2s,
    color 0.2s;
`;

const getColor = (isSelected: boolean, category?: CategoryType | null) => {
  if (isSelected) {
    return category ? theme.colors[category] : theme.colors.green700;
  }

  return theme.colors.gray700;
};
