import styled from 'styled-components';

export const AccountInfoContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1.6rem;
`;

export const AccountInfoItem = styled.div`
  display: flex;
  align-items: center;
  gap: 0.8rem;
`;

export const AccountInfoLabel = styled.span`
  color: ${({ theme }) => theme.colors.gray950};
  ${({ theme }) => theme.fonts.body15Med};
  width: 4.8rem;
`;

export const AccountInfoValue = styled.span`
  font-size: 1.6rem;
  padding: 1.2rem;
  color: ${({ theme }) => theme.colors.gray400};
  background-color: ${({ theme }) => theme.colors.gray50};
  ${({ theme }) => theme.fonts.label14Reg};
  flex-grow: 1;
  border-radius: 0.8rem;
`;
