import styled from 'styled-components';

export const SearchBarContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;

  width: 100%;
  height: 5rem;

  background-color: ${({ theme }) => theme.colors.white};
  border-radius: 1.2rem;
  padding: 0 1.2rem;
  gap: 0.8rem;
`;

export const SearchInput = styled.input`
  flex-grow: 1;
  outline: none;
  border: none;
  ${({ theme }) => theme.fonts.label14Med};
  color: ${({ theme }) => theme.colors.gray900};

  &::placeholder {
    color: ${({ theme }) => theme.colors.gray400};
  }
`;
