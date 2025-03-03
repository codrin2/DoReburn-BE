import styled from 'styled-components';

import IconButton from '@/components/Button/IconButton';

export const ShortcutButtonContainer = styled.div`
  display: flex;
  gap: 2.4rem;
  align-self: flex-start;
`;

export const IconButtonWrapper = styled(IconButton)`
  display: flex;
  gap: 0.4rem;
  color: ${({ theme }) => theme.colors.green500};
`;
