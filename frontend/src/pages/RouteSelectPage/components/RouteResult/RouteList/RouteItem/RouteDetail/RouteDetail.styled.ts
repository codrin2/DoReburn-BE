import styled from 'styled-components';

export const RouteDetailList = styled.div`
  display: flex;
  flex-direction: column;
  width: 100%;
  gap: 1.2rem;
`;

export const RouteDetailItem = styled.div`
  display: flex;
  flex-direction: row;
  width: 100%;
`;

export const DetailItemLabel = styled.div`
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 0.4rem;
  height: fit-content;
`;

export const DetailItemType = styled.div<{ $color: string }>`
  width: 4rem;
  color: ${({ $color }) => $color};
  ${({ theme }) => theme.fonts.label14Semi};
  width: 7rem;
`;

export const DetailContentWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 0.4rem;
`;

export const DetailItemPath = styled.div`
  color: ${({ theme }) => theme.colors.gray500};
  ${({ theme }) => theme.fonts.label14Med};
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  word-break: break-all;
`;

export const BusNumber = styled.div<{ $color: string }>`
  color: ${({ $color }) => $color};

  ${({ theme }) => theme.fonts.caption11};
  font-size: 1rem;
  border: 0.1rem solid ${({ $color }) => $color};
  border-radius: 0.4rem;
  padding: 0.2rem 0.4rem;
  width: fit-content;
`;
