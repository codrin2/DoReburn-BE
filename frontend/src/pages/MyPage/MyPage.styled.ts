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
  height: 100%;
`;

export const Divider = styled.div`
  width: 100%;
  height: 0.15rem;
  background-color: ${({ theme }) => theme.colors.gray200};
  border-radius: 0.6rem;
`;
