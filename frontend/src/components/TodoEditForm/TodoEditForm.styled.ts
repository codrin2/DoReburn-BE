import styled from 'styled-components';

export const TodoInputWrapper = styled.div`
  display: flex;
  align-items: center;
  gap: 0.8rem;
  padding: 0 0.1rem;
`;

export const TodoInputLabel = styled.span<{ $isTop?: boolean }>`
  width: 4.8rem;
  display: flex;

  ${({ $isTop }) =>
    $isTop &&
    `
    align-self: flex-start;
    padding-top: 0.6rem;
  `}

  ${({ theme }) => theme.fonts.body15Med};
  color: ${({ theme }) => theme.colors.gray950};
`;

export const TodoInput = styled.input`
  ${({ theme }) => theme.fonts.body15Med};
  color: ${({ theme }) => theme.colors.gray950};
  flex-grow: 1;

  background-color: ${({ theme }) => theme.colors.gray50};
  border-radius: 0.8rem;
  padding: 1.2rem;

  &:focus {
    outline: 0.1rem solid ${({ theme }) => theme.colors.green600};
    background-color: ${({ theme }) => theme.colors.white};
  }

  &::placeholder {
    ${({ theme }) => theme.fonts.body15Med};
    color: ${({ theme }) => theme.colors.gray400};
  }
`;

export const MemoTextarea = styled.textarea`
  ${({ theme }) => theme.fonts.body15Med};
  color: ${({ theme }) => theme.colors.gray950};
  flex-grow: 1;

  background-color: ${({ theme }) => theme.colors.gray50};
  border-radius: 0.8rem;
  padding: 1.2rem;

  &:focus {
    outline: 0.1rem solid ${({ theme }) => theme.colors.green600};
    background-color: ${({ theme }) => theme.colors.white};
  }

  &::placeholder {
    ${({ theme }) => theme.fonts.body15Med};
    color: ${({ theme }) => theme.colors.gray400};
  }
`;

export const ConfirmButton = styled.button`
  flex-grow: 1;
  padding: 1.3rem 0;
  border-radius: 0.8rem;
  ${({ theme }) => theme.fonts.body16};
  color: ${({ theme }) => theme.colors.white};
  background-color: ${({ theme }) => theme.colors.green600};

  &:disabled {
    cursor: not-allowed;
    background-color: ${({ theme }) => theme.colors.green100};
  }
`;
