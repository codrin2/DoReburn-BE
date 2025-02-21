import styled from 'styled-components';

export const MyPageContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2.4rem;
  height: 100%;
`;

export const MyPageMainContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2.4rem;
  margin: 0 3.2rem;
  position: relative;
  overflow-y: scroll;
`;

export const NotificationInfoContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;
  background-color: ${({ theme }) => theme.colors.gray100};
  border-radius: 1.6rem;
  padding: 1.8rem;
`;

export const NotificationInfoTitle = styled.p`
  display: flex;
  justify-content: space-between;
  ${({ theme }) => theme.fonts.label13};
  color: ${({ theme }) => theme.colors.gray950};
`;

export const NotificationInfoText = styled.p`
  ${({ theme }) => theme.fonts.caption11};
  color: ${({ theme }) => theme.colors.gray700};
`;

export const NotificationInfoTextStrong = styled.strong`
  ${({ theme }) => theme.fonts.caption11};
  color: ${({ theme }) => theme.colors.gray950};
  font-weight: 900;
`;

export const Divider = styled.div`
  width: 100%;
  height: 0.15rem;
  background-color: ${({ theme }) => theme.colors.gray200};
  border-radius: 0.6rem;
`;
