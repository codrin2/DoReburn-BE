import styled from 'styled-components';

export const RouteBarContainer = styled.div`
  display: flex;
  flex-direction: row;
  width: 100%;
  height: 1rem;
  background-color: ${({ theme }) => theme.colors.gray200};
  border-radius: 1rem;
  margin-bottom: 1.6rem;
`;

export const RouteItem = styled.div<{ $barWidth: number }>`
  display: flex;
  position: relative;
  flex-direction: row;
  width: ${({ $barWidth }) => $barWidth}%;
  height: 1rem;
`;

export const IconWrapper = styled.div<{ $color: string }>`
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  top: -0.15rem;
  width: 1.3rem;
  height: 1.3rem;
  background-color: ${({ $color }) => $color};
  border-radius: 50%;
  border: 0.06rem solid ${({ theme }) => theme.colors.gray50};
`;

export const RouteProgressBar = styled.div<{ $isTraffic: boolean; $color: string }>`
  width: 100%;
  height: 100%;
  background-color: ${({ $isTraffic, $color }) => ($isTraffic ? $color : 'transparent')};
  border-radius: 1rem;
  display: flex;
  justify-content: center;
  align-items: center;
`;

export const RouteProgressBarText = styled.div<{ $trafficType: string }>`
  padding-left: 1.2rem;
  display: flex;
  justify-content: center;
  align-items: center;
  color: ${({ theme, $trafficType }) =>
    $trafficType === 'WALK' ? theme.colors.gray950 : theme.colors.gray50};
`;
