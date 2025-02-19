import styled from 'styled-components';

export const AddressContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1.4rem;
`;

export const TitleContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 1.4rem;
`;

export const TitleText = styled.div`
  ${({ theme }) => theme.fonts.body15Med};
  color: ${({ theme }) => theme.colors.gray950};
`;

export const TitleMessage = styled.div`
  ${({ theme }) => theme.fonts.caption12Semi};
  color: ${({ theme }) => theme.colors.green700};
`;

export const AddressList = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;
`;

export const AddressItem = styled.button`
  display: flex;
  align-items: center;
  gap: 0.8rem;
  border: 0.15rem solid ${({ theme }) => theme.colors.gray200};
  border-radius: 1.2rem;
  padding: 1.2rem;
`;

export const AddressLabel = styled.div`
  display: flex;
  align-items: center;
  gap: 0.4rem;
  ${({ theme }) => theme.fonts.body15Med};
  color: ${({ theme }) => theme.colors.green700};
  width: 5.4rem;
`;

export const AddressTitle = styled.div`
  ${({ theme }) => theme.fonts.body15Med};
  color: ${({ theme }) => theme.colors.gray400};
`;
