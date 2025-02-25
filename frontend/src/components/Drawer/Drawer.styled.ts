import { Link } from 'react-router';
import styled from 'styled-components';

export const DrawerLayout = styled.div<{ $isOpen: boolean }>`
  position: absolute;
  top: 0;
  bottom: 0;
  right: 50%;
  transform: translateX(50%);
  width: 100%;
  overflow: hidden;

  height: 100%;
  z-index: 5;
  visibility: ${({ $isOpen }) => ($isOpen ? 'visible' : 'hidden')};
  opacity: ${({ $isOpen }) => ($isOpen ? 1 : 0)};

  transition:
    visibility 0.3s ease-in-out,
    opacity 0.3s ease-in-out;
`;

export const Overlay = styled.div<{ $isOpen: boolean }>`
  position: absolute;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.3);
`;

export const Content = styled.div<{ $isOpen: boolean }>`
  position: absolute;
  top: 0;
  right: 0;

  width: 50%;
  height: 100%;
  padding: 2rem 0;

  background-color: ${({ theme }) => theme.colors.white};
  border-radius: 0 0 0 3.2rem;

  display: flex;
  flex-direction: column;
  gap: 1rem;

  transform: translateX(${({ $isOpen }) => ($isOpen ? '0' : '100%')});
  transition: transform 0.3s ease-in-out;
  will-change: transform;
`;

export const MenuTitle = styled(Link)`
  padding: 1.6rem 1.8rem;
`;

export const MenuList = styled.ul`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  width: 100%;
  gap: 1rem;
`;

export const MenuItem = styled.button`
  width: 100%;
  text-align: left;
  ${({ theme }) => theme.fonts.body16};
  font-weight: 500;
  color: ${({ theme }) => theme.colors.gray950};
  padding: 0.4rem 1.8rem;

  position: relative;
  padding-bottom: 1rem;

  &:not(:last-child)::after {
    content: '';
    display: block;
    width: 85%;
    height: 0.15rem;
    background-color: ${({ theme }) => theme.colors.gray100};

    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
  }

  a {
    display: flex;
    align-items: center;
    gap: 0.8rem;
  }
`;
