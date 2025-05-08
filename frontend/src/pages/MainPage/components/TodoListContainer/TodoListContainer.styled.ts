import styled from 'styled-components';

import IconButton from '@/components/Button/IconButton';

export const TodoListContainerLayout = styled.div`
  display: flex;
  flex-direction: column;
  padding: 1.6rem;
  gap: 1.2rem;
  border-radius: 2.4rem;
  margin: 0 2.4rem;

  background: linear-gradient(
    to bottom,
    ${({ theme }) => theme.colors.lightGreen100} 0%,
    ${({ theme }) => theme.colors.lightWhite30} 100%
  );

  backdrop-filter: blur(3.3rem);
`;

export const TodoList = styled.div<{ $isScroll: boolean }>`
  display: flex;
  align-items: center;
  flex-direction: column;
  gap: 0.8rem;

  max-height: 16.8rem;
  overflow-y: auto;
  padding-right: ${({ $isScroll }) => ($isScroll ? '1.2rem' : '0')};

  &::-webkit-scrollbar {
    width: 0.15rem;
    height: 0.15rem;
  }

  &::-webkit-scrollbar-thumb {
    background-color: ${({ theme }) => theme.colors.gray700};
    border-radius: 1rem;
  }

  &::-webkit-scrollbar-track {
    background-color: ${({ theme }) => theme.colors.gray300};
  }
`;

export const TodoItem = styled.div`
  ${({ theme }) => theme.fonts.body15};
  background-color: ${({ theme }) => theme.colors.white};
  color: ${({ theme }) => theme.colors.gray950};

  width: 100%;
  display: flex;
  gap: 1.2rem;
  border-radius: 1.2rem;
  padding: 1.2rem;
`;

export const TodoTitle = styled.span`
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  word-break: break-all;
`;

export const ContentHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;

  padding: 0 0.35rem 0 0.45rem;
`;

export const ContentTitle = styled.span`
  ${({ theme }) => theme.fonts.body16};
  color: ${({ theme }) => theme.colors.gray600};
`;

export const EditButton = styled(IconButton)`
  display: flex;
  align-items: center;
  gap: 0.4rem;
  padding: 0.4rem 0 0.4rem 2.4rem;

  span {
    ${({ theme }) => theme.fonts.body15};
    color: ${({ theme }) => theme.colors.gray600};
  }
`;

export const EditLabel = styled.span`
  ${({ theme }) => theme.fonts.label13};
  color: ${({ theme }) => theme.colors.gray600};
`;
