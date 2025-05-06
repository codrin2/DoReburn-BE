import styled from 'styled-components';

export const TimeBlockList = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2.4rem;
  overflow: hidden;
  flex-grow: 1;
`;

export const EmptyTimeBlock = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.6rem;
`;

export const EmptyTimeBlockText = styled.span`
  ${({ theme }) => theme.fonts.label13};
  color: ${({ theme }) => theme.colors.gray400};
`;
