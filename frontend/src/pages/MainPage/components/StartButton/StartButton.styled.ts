import styled from 'styled-components';

import IconButton from '@/components/Button/IconButton';

export const StartButtonLayout = styled(IconButton)`
  background-color: ${({ theme }) => theme.colors.green600};
  color: ${({ theme }) => theme.colors.white};

  display: flex;
  padding: 2rem 4rem;
  gap: 0.4rem;
  border-radius: 3.2rem;

  position: absolute;
  bottom: 4rem;

  span {
    ${({ theme }) => theme.fonts.headline18};
  }
`;
