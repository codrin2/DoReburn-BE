import styled, { css } from 'styled-components';

export const TimeBlockItemLayout = styled.div<{
  $isDragging: boolean;
}>`
  position: relative;
  display: flex;
  align-items: center;
  gap: 1.2rem;
  height: 4rem;

  touch-action: ${({ $isDragging }) => ($isDragging ? 'none' : 'pan-y')};
  user-select: none;

  ${({ $isDragging }) =>
    $isDragging &&
    css`
      position: absolute;
      z-index: 1;
      opacity: 0.6;
    `}

  &::after {
    content: '';
    position: absolute;
    left: 0;
    right: 0;
    bottom: -1.2rem;
    height: 0.15rem;
    background-color: ${({ theme }) => theme.colors.gray100};
  }

  &:last-child::after {
    content: none;
  }
`;

export const CheckIconWrapper = styled.div<{ $isDone: boolean; $isAnimating?: boolean }>`
  position: relative;
  width: 2.8rem;
  height: 2.8rem;

  & > div {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    transition:
      opacity 0.5s ease,
      transform 0.5s ease;
  }

  /* EmptyCheck */
  & > div:nth-child(1) {
    opacity: ${({ $isDone, $isAnimating }) => (!$isDone && !$isAnimating ? 1 : 0)};
    transform: scale(${({ $isDone, $isAnimating }) => (!$isDone && !$isAnimating ? 1 : 0.7)});
  }

  /* FilledCheck (0.4초 동안 표시됨) */
  & > div:nth-child(2) {
    opacity: ${({ $isAnimating }) => ($isAnimating ? 1 : 0)};
    transform: scale(${({ $isAnimating }) => ($isAnimating ? 1.1 : 1)});
  }

  /* Category Icon */
  & > div:nth-child(3) {
    opacity: ${({ $isDone }) => ($isDone ? 1 : 0)};
    transform: scale(${({ $isDone }) => ($isDone ? 1 : 1.1)});
  }
`;

export const TimeBlockContent = styled.button`
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  cursor: pointer;
  text-align: left;

  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;

  flex-basis: 85%;
`;

export const TodoTitle = styled.span<{ $isDone: boolean }>`
  ${({ theme }) => theme.fonts.body16};
  color: ${({ theme, $isDone }) => ($isDone ? theme.colors.gray400 : theme.colors.gray950)};
  text-decoration: ${({ $isDone }) => ($isDone ? 'line-through' : 'none')};

  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
`;

export const TodoMemo = styled.span`
  ${({ theme }) => theme.fonts.caption12Reg};
  color: ${({ theme }) => theme.colors.gray300};
`;

export const DraggingItem = styled.div`
  position: fixed;
  pointer-events: none;
  opacity: 0.8;
  background-color: ${({ theme }) => theme.colors.white};
  box-shadow: 0px 4px 4px rgba(0, 0, 0, 0.2);
  transform: translate(-50%, -50%);
  z-index: 1;

  padding: 0.8rem 0.8rem;
  border-radius: 0.8rem;
`;
