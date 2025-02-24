import styled, { keyframes } from 'styled-components';

export const SheetContainer = styled.div`
  position: absolute;
  bottom: 0;

  width: 100%;
  height: 100%;

  z-index: 2;
`;

export const Backdrop = styled.div<{ $isOpen: boolean }>`
  position: absolute;
  background-color: ${({ $isOpen }) => ($isOpen ? 'rgba(0, 0, 0, 0.6)' : 'transparent')};
  width: 100%;
  height: 100%;
`;

export const Sheet = styled.div<{ $isOpen: boolean; $delay: number }>`
  position: absolute;
  width: 100%;
  bottom: 0;
  border-radius: 2.4rem 2.4rem 0 0;
  padding: 0 2.4rem 2rem 2.4rem;

  background-color: ${({ theme }) => theme.colors.white};

  animation: ${({ $isOpen }) => ($isOpen ? slideUp : slideDown)}
    ${({ $delay }) => `${($delay / 1000).toFixed(1)}s`} ease-out forwards;
`;

export const Header = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 2.4rem;
`;

export const DragHandle = styled.div`
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  top: 0;
  width: 4rem;
  height: 0.6rem;
  background-color: ${({ theme }) => theme.colors.gray300};
  border-radius: 1.2rem;
  margin: 0.8rem;
`;

export const Title = styled.h2`
  ${({ theme }) => theme.fonts.headline18};
  color: ${({ theme }) => theme.colors.gray950};
`;

export const SubTitle = styled.h3`
  ${({ theme }) => theme.fonts.body15Med};
  color: ${({ theme }) => theme.colors.gray600};
`;

export const Content = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2.4rem;
  padding-top: 2.4rem;
`;

export const Footer = styled.div`
  display: flex;
  gap: 0.8rem;
  height: 5rem;
`;

export const CancelButton = styled.button`
  flex-grow: 1;
  padding: 1.3rem 0;
  border-radius: 0.8rem;
  ${({ theme }) => theme.fonts.body16};
  color: ${({ theme }) => theme.colors.green600};
  background-color: ${({ theme }) => theme.colors.green50};
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

const slideUp = keyframes`
    from {
      transform: translateY(100%);
    }
    to {
      transform: translateY(0);
    }
  `;

const slideDown = keyframes`
    from {
      transform: translateY(0);
    }
    to {
      transform: translateY(100%);
    }
  `;
