import styled from 'styled-components';

export const RouteResultContainer = styled.div`
  display: flex;
  flex-direction: column;
  width: 100%;
  background-color: ${({ theme }) => theme.colors.white};
  flex-grow: 1;
  border-radius: 3.2rem 3.2rem 0 0;
  overflow: hidden;
`;

export const StartButton = styled.button`
  position: fixed;
  bottom: 4rem;
  left: 50%;
  transform: translateX(-50%);
  width: calc(100% - 3.2rem);
  max-width: 32.7rem;
  ${({ theme }) => theme.fonts.headline17};
  color: ${({ theme }) => theme.colors.white};
  border-radius: 0.8rem;
  padding: 2rem;
  background-color: ${({ theme }) => theme.colors.green600};
  box-shadow: 0 0.4rem 0.8rem 0 rgba(0, 0, 0, 0.1);
  transition: background-color 0.2s;

  &:disabled {
    background-color: ${({ theme }) => theme.colors.gray200};
    color: ${({ theme }) => theme.colors.gray400};
    cursor: not-allowed;
  }
`;
