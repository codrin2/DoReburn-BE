import styled from 'styled-components';

export const MyPageEditableSectionContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2rem;
  position: relative;
  height: 100%;
`;

export const Button = styled.button`
  display: flex;
  align-items: center;
  gap: 0.2rem;
  padding: 0.6rem 1.6rem;
  border-radius: 1rem;
  ${({ theme }) => theme.fonts.body15Med};
`;

export const EditButton = styled(Button)`
  color: ${({ theme }) => theme.colors.gray500};
  margin-left: auto;
`;

export const ButtonContainer = styled.div`
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
`;

export const CancelButton = styled(Button)`
  color: ${({ theme }) => theme.colors.gray600};
  background-color: ${({ theme }) => theme.colors.gray100};
`;

export const ConfirmButton = styled(Button)`
  color: ${({ theme }) => theme.colors.gray100};
  background-color: ${({ theme }) => theme.colors.green700};

  &:disabled {
    opacity: 0.3;
  }
`;
